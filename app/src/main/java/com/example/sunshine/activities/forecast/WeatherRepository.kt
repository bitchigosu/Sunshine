package com.example.sunshine.activities.forecast

import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import com.example.sunshine.R
import com.example.sunshine.SuperApplication
import com.example.sunshine.database.HourlyWeatherEntry
import com.example.sunshine.database.WeatherEntry
import com.example.sunshine.utils.*
import com.google.android.gms.location.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient

class WeatherRepository {

    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var weather: ArrayList<WeatherEntry>
    private lateinit var hourlyWeather: ArrayList<HourlyWeatherEntry>
    private val okHttpClient = OkHttpClient()
    private val appContext = SuperApplication.getContext()
    private val handler = Handler(Looper.getMainLooper())

    private fun insert(weather: WeatherEntry) = InsertAsyncTask().execute(weather)!!
    private fun insertHourly(weather: HourlyWeatherEntry) =
        InsertHourlyAsyncTask().execute(weather)!!


    @Synchronized
    fun getWeatherData(): LiveData<List<WeatherEntry>> {
        if (SunshinePreferences.getPreferredWeatherLocation(appContext)
            == appContext.getString(R.string.current_location)
        ) {
            getLocation {
                val latitude = it.latitude
                val longitude = it.longitude
                getWeatherInfo(
                    latitude = latitude,
                    longitude = longitude
                )
            }
        } else {
            getLocationGeocode(Pref.getString("City", ""))
        }
        notifications()
        mAllWeather = mWeatherDao.getAllWeather()
        return mAllWeather
    }

    @Synchronized
    fun getHourlyWeatherData(): LiveData<List<HourlyWeatherEntry>> {
        mHourlyWeather = mHourlyDao.getHourlyWeather()
        return mHourlyWeather
    }

    private fun getWeatherInfo(latitude: Double, longitude: Double) {
        val httpUrl = (SCHEME + WEATHER_INFO_URL + WEATHER_INFO_API_KEY
                + "$latitude, $longitude"
                ).toHttpUrlOrNull()
        okHttpClient.makeNewCall(httpUrl) {
            weather = JsonUtil.getSimpleWeatherStringsFromJson(appContext, it)
            hourlyWeather = JsonUtil.getHourlyWeatherFromJson(appContext, it)
            for (i in 0 until weather.size) {
                insert(weather[i])
            }
            for (i in 0 until hourlyWeather.size) {
                insertHourly(hourlyWeather[i])
            }
        }
    }

    private fun getLocationGeocode(placeName: String): DoubleArray {
        var latlng = DoubleArray(2)
        val newPlaceName = placeName.replace(" ", "+")
        val httpUrl =
            (SCHEME + GEOCODE_URL + newPlaceName + GEOCODE_API_KEY).toHttpUrlOrNull()

        okHttpClient.makeNewCall(httpUrl) {
            latlng = JsonUtil.getGeocodeFromJson(it)
            getWeatherInfo(
                latitude = latlng[0],
                longitude = latlng[1]
            )
            Log.d(TAG, "onResponse: ${latlng[0]} ${latlng[1]}")
        }
        return latlng
    }

    private fun getLocation(completion: (Location) -> Unit) {
        val context = SuperApplication.getContext()
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                ForecastActivity.getWeakActivity().get() as Activity,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), 34
            )
        }
        locationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(1800000)
            .setFastestInterval(1500000)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        handler.post {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                completion(it)
            }
        }.addOnFailureListener {
            Toast.makeText(
                context,
                context.getString(R.string.cannot_get_current_coordinates),
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            val location = p0?.lastLocation
        }
    }

    companion object {
        private const val TAG = "WeatherRepository"
        private const val SCHEME = "https://"
        private const val WEATHER_INFO_API_KEY = "dff00a22931b903b6168466d0a34cc2c/"
        private const val WEATHER_INFO_URL = "api.darksky.net/forecast/"


        private const val GEOCODE_URL = "api.opencagedata.com/geocode/v1/json?q="
        private const val GEOCODE_API_KEY = "&key=ee53b979dd30468aaeeb7e5d252625cd"


        private class InsertAsyncTask : AsyncTask<WeatherEntry, Unit, Unit>() {
            override fun doInBackground(vararg params: WeatherEntry?): Unit? {
                mWeatherDao.insert(params[0]!!)
                return null
            }
        }

        private class InsertHourlyAsyncTask : AsyncTask<HourlyWeatherEntry, Unit, Unit>() {
            override fun doInBackground(vararg params: HourlyWeatherEntry?): Unit? {
                mHourlyDao.insert(params[0]!!)
                return null
            }
        }
    }
}

