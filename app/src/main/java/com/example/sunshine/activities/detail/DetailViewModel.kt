package com.example.sunshine.activities.detail

import android.arch.lifecycle.ViewModel
import com.example.sunshine.utils.mWeatherDao

class DetailViewModel: ViewModel() {

    fun getWeatherById(position: Int) = mWeatherDao.getWeatherById(position)
}