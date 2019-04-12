package com.example.sunshine.activities.forecast

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.example.sunshine.database.WeatherEntry

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val weatherResult : LiveData<List<WeatherEntry>> = repository.getWeatherData()

    fun getNewWeather(): LiveData<List<WeatherEntry>> = weatherResult
    fun clear() {
        repository.clear()
    }
}