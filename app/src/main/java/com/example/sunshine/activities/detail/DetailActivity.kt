package com.example.sunshine.activities.detail

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.sunshine.R
import com.example.sunshine.activities.settings.SettingsActivity
import com.example.sunshine.utils.mWeatherDao
import com.example.sunshine.utils.JsonUtil
import kotlinx.android.synthetic.main.extra_weather_detail.*
import kotlinx.android.synthetic.main.primary_weather_info.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DetailActivity : AppCompatActivity() {
    private var position: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        position = intent.getIntExtra("WeatherPos", 0)

        GlobalScope.launch {
            val data = mWeatherDao.getWeatherById(position)
            with(this@DetailActivity) {
                val weatherDesc = data.getWeatherDesc()
                forecast_icon.setImageDrawable(ContextCompat.getDrawable(this@DetailActivity,
                    JsonUtil.getSmallArtResourceIdForWeatherCondition(weatherDesc)))
                day_text.text = data.getDate()
                weather_description_text.text = data.getWeatherDesc()
                max_temp_text.text = data.getMaxTemp().toString()
                min_temp_text.text = data.getMinTemp().toString()
                pressure_value.text = data.getPressure().toString()
                wind_value.text = data.getWindSpeed().toString()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.settingsBtn -> {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

}
