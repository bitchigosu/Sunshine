package com.example.sunshine

import android.content.Context

class SunshinePreferences {
    companion object {
        fun getPreferredWeatherLocation(context: Context): String =
            Pref.getString(context.getString(R.string.pref_location_key), "")

        fun getPreferredWeatherUnits(context: Context): String =
            Pref.getString(context.getString(R.string.pref_units_key), "")
    }
}