package com.example.sunshine

import com.example.sunshine.database.AppDatabase

val db = AppDatabase.getInstance(SuperApplication.getContext())
val mWeatherDao = db!!.weatherDao()
var mAllWeather = mWeatherDao.getAllWeather()