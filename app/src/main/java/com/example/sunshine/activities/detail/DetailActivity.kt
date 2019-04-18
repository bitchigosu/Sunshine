package com.example.sunshine.activities.detail

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.example.sunshine.R
import com.example.sunshine.activities.settings.SettingsActivity
import com.example.sunshine.utils.JsonUtil
import com.example.sunshine.utils.mWeatherDao
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.extra_weather_detail.*
import kotlinx.android.synthetic.main.primary_weather_info.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DetailActivity : AppCompatActivity() {
    private var position: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        position = intent.getIntExtra("WeatherPos", 0)

        back_image.setOnClickListener {
            finish()
        }
        settings_image.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        GlobalScope.launch {
            val data = mWeatherDao.getWeatherById(position)
            with(this@DetailActivity) {
                forecast_icon.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@DetailActivity,
                        JsonUtil.getIcon(data.getIconId())
                    )
                )
                day_text.text = data.getDate()
                weather_description_text.text = data.getWeatherDesc()
                max_temp_text.text = data.getMaxTemp().toString() + getString(R.string.temp_symbol)
                min_temp_text.text = data.getMinTemp().toString() + getString(R.string.temp_symbol)
                pressure_value.text = data.getPressure().toString() + " " + getString(R.string.hpa)
                wind_value.text = data.getWindSpeed().toString() + " " + getString(R.string.kph)
                humidity_value.text = data.getHumidity().toString() + getString(R.string.percentage_symbol)
            }
        }
    }
}
