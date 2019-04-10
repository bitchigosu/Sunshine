package com.example.sunshine.utils

import android.content.Context
import android.text.format.DateUtils
import com.example.sunshine.R
import com.example.sunshine.database.WeatherEntry
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
        private const val OWM_LIST = "list"

        /* All temperatures are children of the "temp" object */
        private const val OWM_TEMPERATURE = "temp"

        /* Max temperature for the day */
        private const val OWM_MAX = "max"
        private const val OWM_MIN = "min"

        private const val OMW_PRESSURE = "pressure"
        private const val OWM_SPEED = "speed"

        private const val OWM_WEATHER = "weather"
        private const val OWM_DESCRIPTION = "main"

        private const val OWM_MESSAGE_CODE = "cod"

        private const val OWM_CITY = "city"
        private const val OWM_CITY_NAME = "name"

        @Throws(JSONException::class)
        fun getSimpleWeatherStringsFromJson(context: Context, forecastJsonStr: String): ArrayList<WeatherEntry> {

            /* Weather information. Each day's forecast info is an element of the "list" array */


            /* String array to hold each day's weather String */
            val weatherData = ArrayList<WeatherEntry>()

            val forecastJson = JSONObject(forecastJsonStr)

            /* Is there an error? */
            if (forecastJson.has(OWM_MESSAGE_CODE)) {
                val errorCode = forecastJson.getInt(OWM_MESSAGE_CODE)

                when (errorCode) {
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
            val cityArray = forecastJson.getJSONObject(OWM_CITY)
            val cityName = cityArray.getString(OWM_CITY_NAME)

            val weatherArray = forecastJson.getJSONArray(OWM_LIST)

            val localDate = System.currentTimeMillis()
            val utcDate = getUTCDateFromLocal(localDate)
            val startDay = normalizeDate(utcDate)

            for (i in 0 until weatherArray.length()) {
                /* These are the values that will be collected */
                val high: Int
                val low: Int
                val pressure: Double
                val speed: Double
                val description: String
                val timeInMillis: Long

                /* Get the JSON object representing the day */
                val dayForecast = weatherArray.getJSONObject(i)
                pressure = dayForecast.getDouble(OMW_PRESSURE)
                speed = dayForecast.getDouble(OWM_SPEED)
                timeInMillis = dayForecast.getLong("dt")
                val date = SimpleDateFormat("EEE, MMM d", Locale.UK).format(timeInMillis)

                /*
                 * Description is in a child array called "weather", which is 1 element long.
                 * That element also contains a weather code.
                 */
                val weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0)
                description = weatherObject.getString(OWM_DESCRIPTION)

                /*
                 * Temperatures are sent by Open Weather Map in a child object called "temp".
                 *
                 * Editor's Note: Try not to name variables "temp" when working with temperature.
                 * It confuses everybody. Temp could easily mean any number of things, including
                 * temperature, temporary and is just a bad variable name.
                 */
                val temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE)
                high = (temperatureObject.getDouble(OWM_MAX) - 273.15).toInt()
                low = (temperatureObject.getDouble(OWM_MIN) - 273.15).toInt()
                weatherData.add(
                    WeatherEntry(
                        id = i, city = cityName, date = date,
                        weatherDesc = description, maxTemp = high, minTemp = low, windSpeed = speed, pressure = pressure
                    )
                )

            }

            return weatherData
        }

        fun getSmallArtResourceIdForWeatherCondition(desc: String): Int = when (desc) {
            "Clear" -> R.drawable.art_clear
            "Snow" -> R.drawable.art_snow
            "Cloudy" -> R.drawable.art_clouds
            "Fog" -> R.drawable.art_fog
            "Light Clouds" -> R.drawable.art_light_clouds
            "Light Rain" -> R.drawable.art_light_rain
            "Rain" -> R.drawable.art_rain
            "Storm" -> R.drawable.art_storm
            else -> R.drawable.ic_launcher_background
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

        fun getFriendlyDateString(context: Context, dateInMillis: Long, showFullDate: Boolean): String {

            val localDate = getLocalDateFromUTC(dateInMillis)
            val dayNumber = getDayNumber(localDate)
            val currentDayNumber =
                getDayNumber(System.currentTimeMillis())

            if (dayNumber == currentDayNumber || showFullDate) {
                /*
                 * If the date we're building the String for is today's date, the format
                 * is "Today, June 24"
                 */
                val dayName = getDayName(localDate)
                val readableDate =
                    getReadableDateString(context, localDate)
                if (dayNumber - currentDayNumber < 2) {
                    /*
                     * Since there is no localized format that returns "Today" or "Tomorrow" in the API
                     * levels we have to support, we take the name of the day (from SimpleDateFormat)
                     * and use it to replace the date from DateUtils. This isn't guaranteed to work,
                     * but our testing so far has been conclusively positive.
                     *
                     * For information on a simpler API to use (on API > 18), please check out the
                     * documentation on DateFormat#getBestDateTimePattern(Locale, String)
                     * https://developer.android.com/reference/android/text/format/DateFormat.html#getBestDateTimePattern
                     */
                    val localizedDayName = SimpleDateFormat("EEEE", Locale.UK).format(localDate)
                    return readableDate.replace(localizedDayName, dayName)
                } else {
                    return readableDate
                }
            } else if (dayNumber < currentDayNumber + 7) {
                /* If the input date is less than a week in the future, just return the day name. */
                return getDayName(localDate)
            } else {
                val flags = (DateUtils.FORMAT_SHOW_DATE
                        or DateUtils.FORMAT_NO_YEAR
                        or DateUtils.FORMAT_ABBREV_ALL
                        or DateUtils.FORMAT_SHOW_WEEKDAY)

                return DateUtils.formatDateTime(context, localDate, flags)
            }
        }

        private fun getDayName(dateInMillis: Long): String {
            /*
             * If the date is today, return the localized version of "Today" instead of the actual
             * day name.
             */
            val dayNumber = getDayNumber(dateInMillis)
            val currentDayNumber =
                getDayNumber(System.currentTimeMillis())
            return when (dayNumber) {
                currentDayNumber -> "Today"
                currentDayNumber + 1 -> "Tomorrow"
                else -> {
                    /*
                         * Otherwise, if the day is not today, the format is just the day of the week
                         * (e.g "Wednesday")
                         */
                    val dayFormat = SimpleDateFormat("EEEE", Locale.UK)
                    dayFormat.format(dateInMillis)
                }
            }
        }

        private fun getDayNumber(date: Long): Long {
            val tz = TimeZone.getDefault()
            val gmtOffset = tz.getOffset(date)
            return (date + gmtOffset) / DAY_IN_MILLIS
        }

        private fun getReadableDateString(context: Context, timeInMillis: Long): String {
            val flags = (DateUtils.FORMAT_SHOW_DATE
                    or DateUtils.FORMAT_NO_YEAR
                    or DateUtils.FORMAT_SHOW_WEEKDAY)

            return DateUtils.formatDateTime(context, timeInMillis, flags)
        }

        private fun getLocalDateFromUTC(utcDate: Long): Long {
            val tz = TimeZone.getDefault()
            val gmtOffset = tz.getOffset(utcDate)
            return utcDate - gmtOffset
        }
    }
}