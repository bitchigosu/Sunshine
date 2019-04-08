package com.example.sunshine.activities.forecast

import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.example.sunshine.SuperApplication
import com.example.sunshine.database.AppDatabase
import com.example.sunshine.database.WeatherDao
import com.example.sunshine.database.WeatherEntry
import com.example.sunshine.utils.JsonUtil
import okhttp3.*
import java.io.IOException
class WeatherRepository {
    private val TAG = "WeatherRepository"
    private val DEFAULT_WEATHER_LOCATION = "Moscow"
    private val STATIC_WEATHER_URL = "samples.openweathermap.org"

    private lateinit var weather: ArrayList<WeatherEntry>
    private var mAllWeather: LiveData<List<WeatherEntry>>
    private var mWeatherDao: WeatherDao

    init {
        val db = AppDatabase.getInstance(SuperApplication.getContext())
        mWeatherDao = db!!.weatherDao()
        mAllWeather = mWeatherDao.getAllWeather()
    }

    fun clear(): AsyncTask<WeatherEntry, Unit, Unit> = DeleteAsyncTask(mWeatherDao).execute()

    fun insert(weather: WeatherEntry) = InsertAsyncTask(mWeatherDao).execute(weather)!!

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
                weather = JsonUtil.getSimpleWeatherStringsFromJson(SuperApplication.getContext()
                    ,jsonString)
                for (i in 0 until weather.size) {
                    insert(weather[i])
                }
            }

            override fun onFailure(call: Call, e: IOException) {
            }
        })

        mAllWeather = mWeatherDao.getAllWeather()
        return mAllWeather
    }

    companion object {
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

