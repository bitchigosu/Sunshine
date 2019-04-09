package com.example.sunshine.activities.forecast

import android.app.PendingIntent
import android.arch.lifecycle.LiveData
import android.content.Intent
import android.os.AsyncTask
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.TaskStackBuilder
import android.text.format.DateUtils
import android.util.Log
import com.example.sunshine.R
import com.example.sunshine.SuperApplication
import com.example.sunshine.activities.detail.DetailActivity
import com.example.sunshine.database.WeatherDao
import com.example.sunshine.database.WeatherEntry
import com.example.sunshine.mAllWeather
import com.example.sunshine.mWeatherDao
import com.example.sunshine.utils.JsonUtil
import com.example.sunshine.utils.SunshinePreferences
import okhttp3.*
import java.io.IOException

class WeatherRepository {

    private lateinit var weather: ArrayList<WeatherEntry>

    fun clear(): AsyncTask<WeatherEntry, Unit, Unit> = DeleteAsyncTask(mWeatherDao).execute()

    fun insert(weather: WeatherEntry) = InsertAsyncTask(mWeatherDao).execute(weather)!!

    @Synchronized
    fun getWeatherData(): LiveData<List<WeatherEntry>> {
        Log.d(TAG, "getWeatherData: inside getWeatherData ")
        val client = OkHttpClient()

        val httpUrl = HttpUrl.Builder()
            .scheme("https")
            .host(STATIC_WEATHER_URL)
            .addPathSegment("data")
            .addPathSegment("2.5")
            .addPathSegment("forecast")
            .addPathSegment("daily")
            .addQueryParameter("id", "524901")
            .addQueryParameter("appid", "b1b15e88fa797225412429c1c50c122a1")
            .build()

        val request = Request.Builder()
            .url(httpUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val jsonString = response.body()!!.string()
                Log.d(TAG, "onResponse: $jsonString")
                weather = JsonUtil.getSimpleWeatherStringsFromJson(
                    SuperApplication.getContext()
                    , jsonString
                )
                for (i in 0 until weather.size) {
                    insert(weather[i])
                }
            }

            override fun onFailure(call: Call, e: IOException) {
            }
        })
        notifications()
        mAllWeather = mWeatherDao.getAllWeather()
        return mAllWeather
    }

    private fun notifications() {
        val context = SuperApplication.getContext()
        val notificationsEnabled = SunshinePreferences.areNotificationsEnabled(context)
        val timeSinceLastNotification = SunshinePreferences.getEllapsedTimeSinceLastNotification(context)
        var oneDayPassedSinceLastNotification = false
        if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
            oneDayPassedSinceLastNotification = true
        }

        if (notificationsEnabled and oneDayPassedSinceLastNotification) {
            val notificationBuilder = NotificationCompat.Builder(context, "1")
                .setSmallIcon(R.drawable.notify_panel_notification_icon_bg)
                .setContentTitle("Sunshine")
                .setContentText("New weather available")
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

    companion object {

        private const val TAG = "WeatherRepository"
        private const val DEFAULT_WEATHER_LOCATION = "Moscow"
        private const val STATIC_WEATHER_URL = "samples.openweathermap.org"

        private class InsertAsyncTask(private val mAsyncTaskDao: WeatherDao) : AsyncTask<WeatherEntry, Unit, Unit>() {
            override fun doInBackground(vararg params: WeatherEntry?): Unit? {
                mAsyncTaskDao.insert(params[0]!!)
                return null
            }
        }

        private class DeleteAsyncTask(private val mAsyncTaskDao: WeatherDao) : AsyncTask<WeatherEntry, Unit, Unit>() {
            private val TAG = "DeleteAsyncTask"
            override fun doInBackground(vararg params: WeatherEntry?): Unit? {
                mAsyncTaskDao.deleteAll()
                return null
            }

            override fun onPostExecute(result: Unit?) {
                WeatherRepository().getWeatherData()
            }
        }
    }
}

