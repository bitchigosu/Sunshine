package com.example.sunshine.activities.detail

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.sunshine.R
import com.example.sunshine.ViewModelFactory
import com.example.sunshine.activities.settings.SettingsActivity
import com.example.sunshine.databinding.ActivityDetailBinding
import com.example.sunshine.utils.JsonUtil

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel

    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        subscribeToObservers()
        position = intent.getIntExtra("WeatherPos", 0)
    }

    private fun setupView() {
        with(binding) {
            backImage.setOnClickListener {
                viewModel.onBackClicked()
            }

            settingsImage.setOnClickListener {
                viewModel.onSettingsClicked()
            }
        }
    }

    private fun subscribeToObservers() {
        viewModel = ViewModelProvider(this, ViewModelFactory()).get(DetailViewModel::class.java)
        with(viewModel) {
            goToBackScreen.observe(this@DetailActivity) {
                finish()
            }
            goToSettingsScreen.observe(this@DetailActivity) {
                navigateToSettings()
            }
            getWeatherById(position).observe(this@DetailActivity) {
                it?.let { data ->

                    with(binding.primaryInfo) {
                        forecastIcon.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@DetailActivity,
                                JsonUtil.getIcon(data.getIconId())
                            )
                        )
                        dayText.text = data.getDate()
                        weatherDescriptionText.text = data.getWeatherDesc()
                        maxTempText.text =
                            "${data.getMaxTemp()}${getString(R.string.temp_symbol)}"
                        minTempText.text =
                            "${data.getMinTemp()}${getString(R.string.temp_symbol)}"
                    }

                    with(binding.extraDetails) {
                        sunriseValue.text = data.getSunrise()
                        sunsetValue.text = data.getSunset()
                        humidityValue.text =
                            "${data.getHumidity()}${getString(R.string.percentage_symbol)}"
                        pressureValue.text =
                            "${data.getPressure().toInt()}${getString(R.string.hpa)}"
                        windValue.text = "${data.getWindSpeed()}${getString(R.string.kph)}"
                        uvIndexValue.text = "${data.getUvIndex()}"
                    }
                }

            }
        }
    }

    private fun navigateToSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}


