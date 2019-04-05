package com.example.sunshine.activities.forecast

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.example.sunshine.database.WeatherEntry

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    val weatherResult = MutableLiveData<ArrayList<String>>()

    fun loadWeather(context: Context) {
        repository.getWeatherData(context) { weather ->
            weatherResult.postValue(weather)
        }
    }

    fun updateWeather(data: String){
        repository.addWeatherData(data) { weather ->
            weatherResult.postValue(weather)
        }
    }
}