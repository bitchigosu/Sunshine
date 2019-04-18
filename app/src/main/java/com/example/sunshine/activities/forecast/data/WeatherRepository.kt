package com.example.sunshine.activities.forecast.data

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.example.sunshine.R
import com.example.sunshine.SuperApplication
import com.example.sunshine.activities.forecast.MainActivity
import com.example.sunshine.database.WeatherEntry
import com.example.sunshine.utils.*
import com.google.android.gms.location.*
import okhttp3.*
import java.io.IOException

class WeatherRepository {

    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var weather: ArrayList<WeatherEntry>
    private val mClient = OkHttpClient()
    private val mContext = SuperApplication.getContext()

    fun clear(): AsyncTask<WeatherEntry, Unit, Unit> = DeleteAsyncTask().execute()

    fun insert(weather: WeatherEntry) = InsertAsyncTask().execute(weather)!!

    @Synchronized
    fun getWeatherData(): LiveData<List<WeatherEntry>> {
        if (SunshinePreferences.getPreferredWeatherLocation(mContext)
            == mContext.getString(R.string.current_location)
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

    private fun getWeatherInfo(latitude: Double, longitude: Double) {
        val httpUrl = HttpUrl.parse(SCHEME + WEATHER_INFO_URL + WEATHER_INFO_API_KEY
                + "$latitude, $longitude")

        mClient.newCall(makeRequest(httpUrl)).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val jsonString = response.body()!!.string()
                weather = JsonUtil.getSimpleWeatherStringsFromJson(
                    mContext, jsonString)
                for (i in 0 until weather.size) {
                    insert(weather[i])
                }
            }

            override fun onFailure(call: Call, e: IOException) {
            }
        })
    }

    private fun getLocationGeocode(placeName: String): DoubleArray {
        var latlng = DoubleArray(2)
        val newPlaceName = placeName.replace(" ", "+")
        val httpUrl =
            HttpUrl.parse(SCHEME + GEOCODE_URL + newPlaceName + GEOCODE_API_KEY)

        mClient.newCall(makeRequest(httpUrl)).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val jsonString = response.body()!!.string()
                latlng = JsonUtil.getGeocodeFromJson(jsonString)
                getWeatherInfo(
                    latitude = latlng[0],
                    longitude = latlng[1]
                )
            }

            override fun onFailure(call: Call, e: IOException) {
            }
        })

        return latlng
    }

    private fun getLocation(completion: (Location) -> Unit) {
        val context = SuperApplication.getContext()
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                MainActivity.getWeakActivity().get() as Activity,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION), 34
            )
        }
        mLocationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(1800000)
            .setFastestInterval(1500000)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        MainActivity.getWeakActivity().get()!!.runOnUiThread {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, null)
        }
        mFusedLocationProviderClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                completion(it)
            }
        }.addOnFailureListener {
            Toast.makeText(context, context.getString(R.string.cannot_get_current_coordinates), Toast.LENGTH_LONG).show()
        }
    }

    private fun makeRequest(url: HttpUrl?) : Request = Request.Builder()
        .url(url!!)
        .build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            val location = p0?.lastLocation
            if (location != null) {
            }
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
