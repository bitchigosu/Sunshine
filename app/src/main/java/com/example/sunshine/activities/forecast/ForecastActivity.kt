package com.example.sunshine.activities.forecast

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sunshine.R
import com.example.sunshine.ViewModelFactory
import com.example.sunshine.activities.detail.DetailActivity
import com.example.sunshine.activities.forecast.adapter.HourlyWeatherAdapter
import com.example.sunshine.activities.forecast.adapter.WeatherAdapter
import com.example.sunshine.activities.settings.SettingsActivity
import com.example.sunshine.databinding.ActivityForecastBinding
import com.example.sunshine.utils.Pref
import com.example.sunshine.utils.SunshineSyncUtils
import java.lang.ref.WeakReference

class ForecastActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var hourlyAdapter: HourlyWeatherAdapter
    private lateinit var binding: ActivityForecastBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)
        weakActivity = WeakReference(this)

        weatherAdapter = WeatherAdapter { position ->
            onClickFun(position)
        }
        hourlyAdapter = HourlyWeatherAdapter()

        subscribeToObservers()

        Pref.registerListener(this)
        SunshineSyncUtils.initialize(this)
    }

    private fun setupView() {
        binding.recyclerView.adapter = weatherAdapter
        binding.recyclerView.setHasFixedSize(true)

        binding.swipeRefresh.setColorSchemeResources(
            R.color.refresh_progress_1,
            R.color.refresh_progress_2, R.color.refresh_progress_3
        )
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getNewWeather()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun subscribeToObservers() {
        viewModel = ViewModelProvider(this, ViewModelFactory()).get(WeatherViewModel::class.java)
        viewModel.getNewHourlyWeather().observe(this) {
            hourlyAdapter.updateData(it)
        }
        viewModel.getNewWeather().observe(this) {
            weatherAdapter.updateData(it!!)
        }
        viewModel.goToSettingsScreen.observe(this) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        viewModel.goToTop.observe(this) {
            binding.recyclerView.smoothScrollToPosition(0)
        }
        viewModel.items.observe(this) {
            weatherAdapter.updateData(it)
        }
        viewModel.itemsToday.observe(this) {
            hourlyAdapter.updateData(it)
        }
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
                viewModel.getNewWeather()
            }
        }
    }

    private fun onClickFun(position: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("WeatherPos", position)
        startActivity(intent)
    }

    companion object {
        const val TAG = "MainActivity"
        private lateinit var weakActivity: WeakReference<ForecastActivity>
        fun getWeakActivity() = weakActivity
    }

}