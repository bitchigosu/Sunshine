package com.example.sunshine.utils

import android.content.Context
import com.example.sunshine.R

class SunshinePreferences {
    companion object {
        fun getPreferredWeatherLocation(context: Context): String =
            Pref.getString(
                context.getString(R.string.pref_location_key),
                ""
            )

        fun getPreferredWeatherUnits(context: Context): String =
            Pref.getString(
                context.getString(R.string.pref_units_key),
                ""
            )
    }
}