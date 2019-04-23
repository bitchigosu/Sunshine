package com.example.sunshine.activities.forecast.data

import androidx.lifecycle.ViewModel

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    fun getNewWeather() = repository.getWeatherData()
}