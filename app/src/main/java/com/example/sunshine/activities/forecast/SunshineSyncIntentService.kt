package com.example.sunshine.activities.forecast

import android.app.IntentService
import android.content.Intent

class SunshineSyncIntentService : IntentService(SunshineSyncIntentService::class.simpleName) {
    override fun onHandleIntent(intent: Intent?) {
        WeatherRepository().getWeatherData()
    }
}