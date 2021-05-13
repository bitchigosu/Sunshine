package com.example.sunshine.activities.forecast

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.sunshine.database.WeatherEntry
import com.example.sunshine.utils.SingleLiveEvent

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    val items : LiveData<List<WeatherEntry>> = repository.getWeatherData()
    private val _goToSettingsScreen = SingleLiveEvent<Unit>()
    val goToSettingsScreen : LiveData<Unit> = _goToSettingsScreen

    private val _goToTop = SingleLiveEvent<Unit>()
    val goToTop : LiveData<Unit> = _goToTop

    fun onSettingsClicked () {
        _goToSettingsScreen.call()
    }

    fun onIconClicked() {
        _goToTop.call()
    }

    fun getNewWeather() = repository.getWeatherData()
}