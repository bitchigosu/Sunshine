package com.example.sunshine.utils

import android.content.Context
import org.json.JSONObject
import org.json.JSONException
import java.net.HttpURLConnection
import java.util.*
import android.text.format.DateUtils
import com.example.sunshine.database.DateConverter
import com.example.sunshine.database.WeatherEntry
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList


class JsonUtil {


    companion object {
        const val SECOND_IN_MILLIS = 1000
        const val MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60
        const val HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60
        const val DAY_IN_MILLIS = HOUR_IN_MILLIS * 24

        @Throws(JSONException::class)
        fun getSimpleWeatherStringsFromJson(context: Context, forecastJsonStr: String): ArrayList<WeatherEntry> {

            /* Weather information. Each day's forecast info is an element of the "list" array */
            val OWM_LIST = "list"

            /* All temperatures are children of the "temp" object */
            val OWM_TEMPERATURE = "temp"

            /* Max temperature for the day */
            val OWM_MAX = "max"
            val OWM_MIN = "min"

            val OMW_PRESSURE = "pressure"
            val OWM_SPEED = "speed"

            val OWM_WEATHER = "weather"
            val OWM_DESCRIPTION = "main"

            val OWM_MESSAGE_CODE = "cod"

            val OWM_CITY = "city"
            val OWM_CITY_NAME = "name"

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
                val dateTimeMillis: Long = startDay + DAY_IN_MILLIS * i
                val high: Double
                val low: Double
                val pressure: Double
                val speed: Double
                val description: String
                val timeInMillis: Long

                /* Get the JSON object representing the day */
                val dayForecast = weatherArray.getJSONObject(i)
                pressure = dayForecast.getDouble(OMW_PRESSURE)
                speed = dayForecast.getDouble(OWM_SPEED)
                timeInMillis = dayForecast.getLong("dt")
                val date = SimpleDateFormat("EEE, MMM d, ''yy", Locale.UK).format(timeInMillis)

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
                high = temperatureObject.getDouble(OWM_MAX)
                low = temperatureObject.getDouble(OWM_MIN)
                weatherData.add(WeatherEntry(id = i,city = cityName, date = date,
                    weatherDesc = description, maxTemp = high, minTemp = low, windSpeed = speed, pressure = pressure))

            }

            return weatherData
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