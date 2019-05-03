package com.example.sunshine.activities.forecast

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sunshine.R
import com.example.sunshine.ViewModelFactory
import com.example.sunshine.activities.detail.DetailActivity
import com.example.sunshine.activities.forecast.adapter.WeatherAdapter
import com.example.sunshine.activities.settings.SettingsActivity
import com.example.sunshine.databinding.ActivityForecastBinding
import com.example.sunshine.utils.Pref
import com.example.sunshine.utils.SunshineSyncUtils
import kotlinx.android.synthetic.main.activity_forecast.*
import java.lang.ref.WeakReference

class ForecastActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var mViewModel: WeatherViewModel
    private lateinit var mAdapter: WeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weakActivity = WeakReference(this)

        val binding: ActivityForecastBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_forecast)
        mViewModel = ViewModelProviders.of(this, ViewModelFactory()).get(WeatherViewModel::class.java)
        mAdapter = WeatherAdapter { position ->
            onClickFun(position)
        }

        binding.viewModel = mViewModel

        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        mViewModel.getNewWeather().observe(this, Observer {
            mAdapter.updateData(it!!)
        })

        mViewModel.goToSettingsScreen.observe(this, Observer {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        })
        mViewModel.goToTop.observe(this, Observer {
            recyclerView.smoothScrollToPosition(0)
        })

        Pref.registerListener(this)
        SunshineSyncUtils.initialize(this)

        swipeRefresh.setColorSchemeResources(
            R.color.refresh_progress_1,
            R.color.refresh_progress_2, R.color.refresh_progress_3
        )
        swipeRefresh.setOnRefreshListener {
            mViewModel.getNewWeather()
            swipeRefresh.isRefreshing = false
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
                mViewModel.getNewWeather()
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