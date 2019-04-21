package com.example.sunshine

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.sunshine.activities.detail.DetailViewModel
import com.example.sunshine.activities.forecast.data.WeatherRepository
import com.example.sunshine.activities.forecast.data.WeatherViewModel
import com.example.sunshine.activities.newlocation.NewLocationRepository
import com.example.sunshine.activities.newlocation.NewLocationViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory :ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java))
            return WeatherViewModel(WeatherRepository()) as T
        else if (modelClass.isAssignableFrom(DetailViewModel::class.java))
            return DetailViewModel() as T
        else if (modelClass.isAssignableFrom(NewLocationViewModel::class.java))
            return NewLocationViewModel(NewLocationRepository()) as T
        else
            error("Unknown View Model class $modelClass")
    }
}