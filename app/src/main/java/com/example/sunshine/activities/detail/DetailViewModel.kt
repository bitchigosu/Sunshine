package com.example.sunshine.activities.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.sunshine.utils.SingleLiveEvent
import com.example.sunshine.utils.mWeatherDao

class DetailViewModel: ViewModel() {

    private val _goToSettingsScreen = SingleLiveEvent<Unit>()
    val goToSettingsScreen : LiveData<Unit> = _goToSettingsScreen

    private val _goToBackScreen = SingleLiveEvent<Unit>()
    val goToBackScreen : LiveData<Unit> = _goToBackScreen

    fun onSettingsClicked () {
        _goToSettingsScreen.call()
    }

    fun onBackClicked() {
        _goToBackScreen.call()
    }
    fun getWeatherById(position: Int) = mWeatherDao.getWeatherById(position)
}