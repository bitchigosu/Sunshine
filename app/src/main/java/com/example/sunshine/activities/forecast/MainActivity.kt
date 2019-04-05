package com.example.sunshine.activities.forecast

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.sunshine.R
import com.example.sunshine.activities.ViewModelFactory
import com.example.sunshine.activities.detail.DetailActivity
import com.example.sunshine.activities.settings.SettingsActivity
import com.example.sunshine.database.AppDatabase
import com.example.sunshine.database.DateConverter
import com.example.sunshine.database.WeatherDao
import com.example.sunshine.database.WeatherEntry
import com.example.sunshine.utils.Pref
import com.example.sunshine.utils.SunshinePreferences
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG = "MainActivity"
    private val DEFAULT_WEATHER_COORDINATES: DoubleArray = DoubleArray(2)
    private lateinit var mViewModel: WeatherViewModel
    private lateinit var mAdapter: WeatherAdapter
    private var list = ArrayList<String>()

    private var db: AppDatabase? = null
    private var weatherDao: WeatherDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Pref.registerListener(this)

        mViewModel = ViewModelProviders.of(this,
            ViewModelFactory()).get(WeatherViewModel::class.java)
        mViewModel.loadWeather(this)

        mAdapter = WeatherAdapter({position ->
            onClickFun(position)},
            list)
        recyclerView.adapter = mAdapter

        setProgressBar(true)

        mViewModel.weatherResult.observe(this, Observer {
            it?.let { it1 ->
                list = it1
                observe()
                setProgressBar(false)
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        DEFAULT_WEATHER_COORDINATES[0] = 37.4284
        DEFAULT_WEATHER_COORDINATES[1] = 122.0724

    }

    override fun onDestroy() {
        super.onDestroy()
        Pref.unregisterListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            getString(R.string.pref_location_key) -> {
                error_message.text = "Location is ${SunshinePreferences.getPreferredWeatherLocation(this)}"
            }

            getString(R.string.pref_units_key) -> {
                Toast.makeText(
                    this,
                    "Units is ${SunshinePreferences.getPreferredWeatherUnits(this)}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun observe() {
        Observable.fromCallable{
            db = AppDatabase.getInstance(this)
            weatherDao = db?.weatherDao()
            weatherDao?.delete()

            for (i in 0 until list.size) {
                val entry = takeSmth(i)!!
                Log.d(TAG, "onCreate: $entry")
                val weather1 = WeatherEntry(id = i,
                    city = entry[0],
                    date = DateConverter().toDate(entry[1].toLong())!!,
                    weatherDesc = entry[2],
                    maxTemp = entry[3].toDouble(),
                    minTemp = entry[4].toDouble(),
                    windSpeed = entry[5].toDouble(),
                    pressure = entry[6].toDouble()
                )

                with(weatherDao) {
                    this?.insert(weather1)
                    Log.d(TAG, "observe: ${this?.getRowCount()}")
                }
            }
            Log.d(TAG, "observe: ${db?.weatherDao()?.getRowCount()}")
            db?.weatherDao()?.loadAllWeather()
        }.doOnNext {
            mAdapter.updateData(it!!)
            var finalString = ""
            it?.map { we ->
                finalString += " - " }
            error_message.text = finalString
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext(Observable.empty())
            .subscribe()
    }

    private fun onClickFun(position: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.btn_searchBar -> {
            observe()
            true
        }

        R.id.settingsBtn -> {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun takeSmth(index: Int): List<String>? {
        val string: String? = list[index]
        return string?.split(" - ")
    }

    private fun setProgressBar(b: Boolean) {
        runOnUiThread {
            if (b) progressBar.visibility = View.VISIBLE
            else progressBar.visibility = View.INVISIBLE
        }
    }
}