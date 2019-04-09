package com.example.sunshine.activities.forecast

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
import com.example.sunshine.R
import com.example.sunshine.ViewModelFactory
import com.example.sunshine.activities.detail.DetailActivity
import com.example.sunshine.activities.settings.SettingsActivity

import com.example.sunshine.utils.Pref
import com.example.sunshine.utils.SunshinePreferences
import com.example.sunshine.utils.SunshineSyncUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG = "MainActivity"
    private val DEFAULT_WEATHER_COORDINATES: DoubleArray = DoubleArray(2)
    private lateinit var mViewModel: WeatherViewModel
    private lateinit var mAdapter: WeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Pref.registerListener(this)

        SunshineSyncUtils.initialize(this)

        mAdapter = WeatherAdapter { position ->
            onClickFun(position)
        }
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        setProgressBar(true)
        mViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory()
        ).get(WeatherViewModel::class.java)
        mViewModel.getCachedWeather().observe(this, Observer {
            mAdapter.updateData(it!!)
            setProgressBar(false)
        })


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
        intent.putExtra("WeatherPos", position)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.btn_searchBar -> {
            mViewModel.clear()
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