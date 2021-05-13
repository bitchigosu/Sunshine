package com.example.sunshine.utils

import android.app.PendingIntent
import android.content.Intent
import android.text.format.DateUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.example.sunshine.R
import com.example.sunshine.SuperApplication
import com.example.sunshine.activities.detail.DetailActivity
import com.example.sunshine.database.AppDatabase
import okhttp3.*
import java.io.IOException

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

fun OkHttpClient.makeNewCall(url: HttpUrl?, f: (jsonString: String) -> Unit) {
    newCall(makeRequest(url)).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            val jsonString = response.body?.string().orEmpty()
            f(jsonString)
        }

        override fun onFailure(call: Call, e: IOException) {
            Log.d("CommonStaticTAG", "onFailure: ${e.message}")
        }

    })
}

private fun makeRequest(url: HttpUrl?): Request = Request.Builder()
    .url(url!!)
    .build()