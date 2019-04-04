package com.example.sunshine

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val DEFAULT_WEATHER_COORDINATES: DoubleArray = DoubleArray(2)
    private lateinit var mViewModel: WeatherViewModel
    private lateinit var mAdapter: WeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Pref.registerListener(this)

        mViewModel = ViewModelProviders.of(this, ViewModelFactory()).get(WeatherViewModel::class.java)
        mViewModel.loadWeather(this)
        setProgressBar(true)
        mViewModel.weatherResult.observe(this, Observer { it ->
            it?.let { it1 ->
                mAdapter = WeatherAdapter({ position ->
                    onClickFun(position)
                }, it1)
                recyclerView.adapter = mAdapter
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
            mViewModel.updateWeather("Swag")
            setProgressBar(true)
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

    private fun setProgressBar(b: Boolean) {
        runOnUiThread {
            if (b) progressBar.visibility = View.VISIBLE
            else progressBar.visibility = View.INVISIBLE
        }
    }
}
