package com.example.sunshine.utils

import android.app.IntentService
import android.content.Intent
import com.example.sunshine.activities.forecast.data.WeatherRepository

class SunshineSyncIntentService : IntentService(SunshineSyncIntentService::class.simpleName) {
    override fun onHandleIntent(intent: Intent?) {
        WeatherRepository().getWeatherData()
    }
}