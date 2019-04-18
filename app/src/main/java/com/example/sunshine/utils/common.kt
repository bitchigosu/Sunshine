package com.example.sunshine.utils

import android.app.PendingIntent
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.TaskStackBuilder
import android.text.format.DateUtils
import com.example.sunshine.R
import com.example.sunshine.SuperApplication
import com.example.sunshine.activities.detail.DetailActivity
import com.example.sunshine.database.AppDatabase

val db = AppDatabase.getInstance(SuperApplication.getContext())
val mWeatherDao = db!!.weatherDao()
var mAllWeather = mWeatherDao.getAllWeather()

fun notifications() {
    val context = SuperApplication.getContext()
    val notificationsEnabled = SunshinePreferences.areNotificationsEnabled(context)
    val timeSinceLastNotification = SunshinePreferences.getElapsedTimeSinceLastNotification(context)
    var oneDayPassedSinceLastNotification = false
    if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
        oneDayPassedSinceLastNotification = true
    }

    if (notificationsEnabled and oneDayPassedSinceLastNotification) {
        val notificationBuilder = NotificationCompat.Builder(context, "1")
            .setSmallIcon(R.drawable.art_clear)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.new_weather_available))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val detailIntentForToday = Intent(context, DetailActivity::class.java)
        val taskStackBuilder = TaskStackBuilder.create(context)
        taskStackBuilder.addNextIntentWithParentStack(detailIntentForToday)
        val resultIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(resultIntent)
        with(NotificationManagerCompat.from(context)) {
            notify(3004, notificationBuilder.build())
        }
        SunshinePreferences.saveLastNotificationTime(context, System.currentTimeMillis())
    }
}