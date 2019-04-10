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
                "si"
            )

        fun areNotificationsEnabled(context: Context): Boolean {
            val displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key)
            val shouldDisplayNotificationsByDefault = context.resources.getBoolean(R.bool.show_notifications_by_default)
            return Pref.getBoolean(displayNotificationsKey, shouldDisplayNotificationsByDefault)
        }

        private fun getLastNotificationTimeInMillis(context: Context): Long {
            val lastNotificationKey = context.getString(R.string.pref_last_notification)
            return Pref.getLong(lastNotificationKey, 0)
        }

        fun getEllapsedTimeSinceLastNotification(context: Context): Long {
            val lastNotificationTimeMillis = SunshinePreferences.getLastNotificationTimeInMillis(context)
            return System.currentTimeMillis() - lastNotificationTimeMillis
        }

        fun saveLastNotificationTime(context: Context, timeOfNotification: Long) {
            val lastNotificationKey = context.getString(R.string.pref_last_notification)
            Pref.setLong(lastNotificationKey,timeOfNotification)
        }
    }
}