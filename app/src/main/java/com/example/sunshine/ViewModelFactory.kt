package com.example.sunshine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sunshine.activities.detail.DetailViewModel
import com.example.sunshine.activities.forecast.data.WeatherRepository
import com.example.sunshine.activities.forecast.data.WeatherViewModel
import com.example.sunshine.activities.newlocation.NewLocationRepository
import com.example.sunshine.activities.newlocation.NewLocationViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(WeatherViewModel::class.java) -> WeatherViewModel(WeatherRepository()) as T
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> DetailViewModel() as T
            modelClass.isAssignableFrom(NewLocationViewModel::class.java) -> NewLocationViewModel(NewLocationRepository()) as T
            else -> error("Unknown View Model class $modelClass")
        }
    }
}