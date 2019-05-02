package com.example.sunshine.activities.detail

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.sunshine.R
import com.example.sunshine.ViewModelFactory
import com.example.sunshine.activities.settings.SettingsActivity
import com.example.sunshine.databinding.ActivityDetailBinding
import com.example.sunshine.utils.JsonUtil
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.extra_weather_detail.*
import kotlinx.android.synthetic.main.primary_weather_info.*


class DetailActivity : AppCompatActivity() {

    private var position: Int = 0
    private lateinit var mViewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        position = intent.getIntExtra("WeatherPos", 0)

        back_image.setOnClickListener {
            finish()
        }
        settings_image.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        mViewModel = ViewModelProviders.of(this, ViewModelFactory()).get(DetailViewModel::class.java)
        mViewModel.getWeatherById(position).observe(this, Observer {
            it?.let { data ->
                with(this@DetailActivity) {
                    forecast_icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@DetailActivity,
                            JsonUtil.getIcon(data.getIconId())
                        )
                    )
                    with(binding.primaryInfo) {
                        dayText.text = data.getDate()
                        weatherDescriptionText.text = data.getWeatherDesc()
                        maxTempText.text = data.getMaxTemp().toString() + getString(R.string.temp_symbol)
                        minTempText.text = data.getMinTemp().toString() + getString(R.string.temp_symbol)
                    }

                    with(binding.extraDetails) {
                        sunriseValue.text = data.getSunrise()
                        sunsetValue.text = data.getSunset()
                        humidityValue.text = data.getHumidity().toString() + getString(R.string.percentage_symbol)
                        pressureValue.text = data.getPressure().toString()
                        windValue.text = data.getWindSpeed().toString() + getString(R.string.kph)
                        uvIndexValue.text = data.getUvIndex().toString()
                    }
                }
            }
        })
    }
}
