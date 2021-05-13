package com.example.sunshine.utils

import android.content.Context
import com.example.sunshine.R
import com.example.sunshine.database.WeatherEntry
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import org.json.JSONException
import org.json.JSONObject
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class JsonUtil {
    companion object {
        private const val SECOND_IN_MILLIS = 1000
        private const val MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60
        private const val HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60
        private const val DAY_IN_MILLIS = HOUR_IN_MILLIS * 24

        private const val OWM_MESSAGE_CODE = "cod"

        @Throws(JSONException::class)
        fun getSimpleWeatherStringsFromJson(
            context: Context,
            forecastJsonStr: String
        ): ArrayList<WeatherEntry> {

            /* Weather information. Each day's forecast info is an element of the "list" array */


            /* String array to hold each day's weather String */
            val weatherData = ArrayList<WeatherEntry>()

            val forecastJson = JSONObject(forecastJsonStr)

            /* Is there an error? */
            if (forecastJson.has(OWM_MESSAGE_CODE)) {

                when (forecastJson.getInt(OWM_MESSAGE_CODE)) {
                    HttpURLConnection.HTTP_OK -> {
                    }
                    HttpURLConnection.HTTP_NOT_FOUND ->
                        /* Location invalid */
                        return ArrayList()
                    else ->
                        /* Server probably down */
                        return ArrayList()
                }
            }
            val daily = forecastJson.getJSONObject("daily")
            val cityName = forecastJson.getString("timezone")
            val data = daily.getJSONArray("data")
            val localDate = System.currentTimeMillis()
            val utcDate = getUTCDateFromLocal(localDate)
            val startDay = normalizeDate(utcDate)
            val timeSDF = SimpleDateFormat("hh:mm", Locale.UK)
            for (i in 0 until data.length()) {
                val dayForecast = data.getJSONObject(i)
                val weatherDesc = dayForecast.getString("summary")
                var tempHigh = dayForecast.getDouble("temperatureHigh").toInt()
                var tempLow = dayForecast.getDouble("temperatureLow").toInt()
                val pressure = dayForecast.getDouble("pressure")
                val windSpeed = dayForecast.getDouble("windSpeed")
                var humidity = dayForecast.getDouble("humidity")
                val uvIndex = dayForecast.getInt("uvIndex")
                val sunrise = dayForecast.getLong("sunriseTime")
                val sunset = dayForecast.getLong("sunsetTime")
                humidity *= 100
                val icon = dayForecast.getString("icon")
                val date =
                    SimpleDateFormat("EEE, MMM d", Locale.UK).format(startDay + DAY_IN_MILLIS * i)
                val sunriseTime = timeSDF.format(sunrise * 1000)
                val sunsetTime = timeSDF.format(sunset * 1000)

                if (SunshinePreferences.getPreferredWeatherUnits(context)) {
                    tempHigh = convertTemperature(tempHigh)
                    tempLow = convertTemperature(tempLow)
                }
                weatherData.add(
                    WeatherEntry(
                        id = i,
                        city = cityName,
                        date = date,
                        weatherDesc = weatherDesc,
                        maxTemp = tempHigh,
                        minTemp = tempLow,
                        pressure = pressure,
                        windSpeed = windSpeed,
                        humidity = humidity.toInt(),
                        sunrise = sunriseTime,
                        sunset = sunsetTime,
                        uvIndex = uvIndex,
                        iconId = icon
                    )
                )
            }
            return weatherData
        }

        @Throws(JSONException::class)
        fun getGeocodeFromJson(geocodeJSON: String): DoubleArray {
            val array = DoubleArray(2)
            val decode = JSONObject(geocodeJSON)
            val results = decode.getJSONArray("results")
            if (results.isNull(0)) {
                array[0] = 0.0
                array[1] = 0.0
                return array
            }
            val n = results.getJSONObject(0)
            val geometry = n.getJSONObject("geometry")
            val lat = geometry.getDouble("lat")
            val lng = geometry.getDouble("lng")
            array[0] = lat
            array[1] = lng
            return array
        }

        @Throws(JSONException::class)
        fun getCitiesFromJson(json: String): Task<ArrayList<String>> {
            val list = ArrayList<String>()
            val decode = JSONObject(json)
            val results = decode.getJSONArray("results")
            if (results.length() != 0) {
                for (i in 0 until results.length()) {
                    list.add(results.getJSONObject(i).getString("formatted"))
                }
            }
            return Tasks.forResult(list)
        }

        private fun convertTemperature(temp: Int): Int = ((temp - 32) * 5 / 9)

        fun getIcon(desc: String): Int = when (desc) {
            "clear-day" -> R.drawable.art_clear
            "clear-night" -> R.drawable.art_clear
            "partly-cloudy-day" -> R.drawable.art_light_clouds
            "partly-cloudy-night" -> R.drawable.art_light_clouds
            "cloudy" -> R.drawable.art_clouds
            "rain" -> R.drawable.art_rain
            "snow" -> R.drawable.art_snow
            "sleet" -> R.drawable.art_rain
            "fog" -> R.drawable.art_fog
            "wind" -> R.drawable.art_storm
            else -> R.drawable.art_clear
        }

        private fun getUTCDateFromLocal(localDate: Long): Long {
            val tz = TimeZone.getDefault()
            val gmtOffset = tz.getOffset(localDate)
            return localDate + gmtOffset
        }

        private fun normalizeDate(date: Long): Long {
            // Normalize the start date to the beginning of the (UTC) day in local time
            return date / DAY_IN_MILLIS * DAY_IN_MILLIS
        }
    }
}
