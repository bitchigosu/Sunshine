package com.example.sunshine.activities

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.sunshine.activities.forecast.WeatherRepository
import com.example.sunshine.activities.forecast.WeatherViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory :ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java))
            return WeatherViewModel(WeatherRepository()) as T
        else
            error("Unknown View Model class $modelClass")
    }
}