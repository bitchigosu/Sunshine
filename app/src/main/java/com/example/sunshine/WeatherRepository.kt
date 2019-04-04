package com.example.sunshine

import android.content.Context
import android.util.Log
import okhttp3.*
import java.io.IOException

class WeatherRepository {
    private val TAG = "WeatherRepository"
    private val DEFAULT_WEATHER_LOCATION = "Moscow"
    private val STATIC_WEATHER_URL = "samples.openweathermap.org"

    private lateinit var weather: MutableList<String>

    fun getWeatherData(context:Context, completion: (MutableList<String>) -> Unit) {
        val client = OkHttpClient()

        val httpUrl = HttpUrl.Builder()
            .scheme("https")
            .host(STATIC_WEATHER_URL)
            .addPathSegment("data")
            .addPathSegment("2.5")
            .addPathSegment("forecast")
            .addPathSegment("daily")
            .addQueryParameter("id","524901")
            .addQueryParameter("appid","b1b15e88fa797225412429c1c50c122a1")
            .build()

        val request = Request.Builder()
            .url(httpUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val jsonString = response.body()!!.string()
                Log.d(TAG, "onResponse: $jsonString")
                weather = JsonUtil.getSimpleWeatherStringsFromJson(context, jsonString)
                completion(weather)
            }

            override fun onFailure(call: Call, e: IOException) {
                completion(mutableListOf())
            }
        })
    }

    fun addWeatherData(data: String, completion: (MutableList<String>) -> Unit) {
        weather.add(data)
        completion(weather)
    }
}