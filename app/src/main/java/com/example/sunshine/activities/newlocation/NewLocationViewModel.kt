package com.example.sunshine.activities.newlocation

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class NewLocationViewModel(private val newLocationRepo: NewLocationRepository) : ViewModel() {
    private val _cities = MutableLiveData<List<String>>()
    val cities: LiveData<List<String>> = _cities

    fun searchCityByName(cityName: String) {
        newLocationRepo.getQueryResults(cityName) {
            _cities.value = it
        }
    }

}