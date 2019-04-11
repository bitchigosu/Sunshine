package com.example.sunshine.activities.forecast

import android.app.Activity
import android.app.PendingIntent
import android.arch.lifecycle.LiveData
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.TaskStackBuilder
import android.text.format.DateUtils
import android.util.Log
import com.example.sunshine.R
import com.example.sunshine.SuperApplication
import com.example.sunshine.activities.detail.DetailActivity
import com.example.sunshine.database.WeatherEntry
import com.example.sunshine.utils.*

import com.google.android.gms.location.*
import okhttp3.*
import java.io.IOException

class WeatherRepository {

    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var weather: ArrayList<WeatherEntry>

    fun clear(): AsyncTask<WeatherEntry, Unit, Unit> = DeleteAsyncTask().execute()

    fun insert(weather: WeatherEntry) = InsertAsyncTask().execute(weather)!!

    @Synchronized
    fun getWeatherData(): LiveData<List<WeatherEntry>> {
        val client = OkHttpClient()
        val context = SuperApplication.getContext()

        val httpUrl = HttpUrl.Builder()
            .scheme("https")
            .host(STATIC_WEATHER_URL)
            .addPathSegment("forecast")
            .addPathSegment("dff00a22931b903b6168466d0a34cc2c")
            .addPathSegment("55.751244,37.618423")
            .build()

        val request = Request.Builder()
            .url(httpUrl)
            .build()
        Log.d(TAG, "getWeatherData: $request")

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val jsonString = response.body()!!.string()
                Log.d(TAG, "onResponse: $jsonString")
                weather = JsonUtil.getSimpleWeatherStringsFromJson(
                    context
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
        val timeSinceLastNotification = SunshinePreferences.getElapsedTimeSinceLastNotification(context)
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

    fun getLocation(activity: Activity) {
        val context = SuperApplication.getContext()
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), 34
            )
        }
        mLocationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(10 * 1000)
            .setFastestInterval(2000)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        mFusedLocationProviderClient.lastLocation.addOnSuccessListener {
            Log.d(TAG, "onLocationResult: ${it.longitude} and ${it.latitude}")
        }
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, null)


    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            val location = p0?.lastLocation
            if (location != null) {
                Log.d(TAG, "onLocationResult: ${location.longitude} and ${location.latitude}")
            }
        }
    }

    companion object {

        private const val TAG = "WeatherRepository"
        private const val DEFAULT_WEATHER_LOCATION = "Moscow"
        private const val STATIC_WEATHER_URL = "api.darksky.net"

        private class InsertAsyncTask : AsyncTask<WeatherEntry, Unit, Unit>() {
            override fun doInBackground(vararg params: WeatherEntry?): Unit? {
                mWeatherDao.insert(params[0]!!)
                return null
            }
        }

        private class DeleteAsyncTask : AsyncTask<WeatherEntry, Unit, Unit>() {
            private val TAG = "DeleteAsyncTask"
            override fun doInBackground(vararg params: WeatherEntry?): Unit? {
                mWeatherDao.deleteAll()
                return null
            }

            override fun onPostExecute(result: Unit?) {
                WeatherRepository().getWeatherData()
            }
        }
    }
}

