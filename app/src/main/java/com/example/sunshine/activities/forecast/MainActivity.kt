package com.example.sunshine.activities.forecast

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.example.sunshine.R
import com.example.sunshine.ViewModelFactory
import com.example.sunshine.activities.detail.DetailActivity
import com.example.sunshine.activities.forecast.data.WeatherViewModel
import com.example.sunshine.activities.settings.SettingsActivity

import com.example.sunshine.utils.Pref
import com.example.sunshine.utils.SunshineSyncUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val TAG = "MainActivity"
    private lateinit var mViewModel: WeatherViewModel
    private lateinit var mAdapter: WeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Pref.registerListener(this)
        SunshineSyncUtils.initialize(this)
        weakActivity = WeakReference(this)

        mAdapter = WeatherAdapter { position ->
            onClickFun(position)
        }

        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        mViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory()
        ).get(WeatherViewModel::class.java)
        mViewModel.getNewWeather().observe(this, Observer {
            mAdapter.updateData(it!!)
        })

        refresh_image.setOnClickListener {
            mViewModel.clear()
        }
        settings_image.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
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
                mViewModel.clear()
            }
        }
    }

    private fun onClickFun(position: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("WeatherPos", position)
        startActivity(intent)
    }

    companion object {
        private lateinit var weakActivity: WeakReference<MainActivity>
        fun getWeakActivity() = weakActivity
    }

}