package com.example.sunshine.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hourlyWeather")
class HourlyWeatherEntry(
    @PrimaryKey
    private var id: Int,
    private var time: String,
    private var temperature: Int,
   // private var sunrise: String,
    //private var sunset: String,
    private var iconId: String
) {


    fun getId(): Int? = id
    fun setId(id: Int) {
        this.id = id
    }

    fun getTime(): String = time
    fun setTime(time: String) {
        this.time = time
    }

    fun getTemperature(): Int = temperature
    fun setTemperature(temperature: Int) {
        this.temperature = temperature
    }

   /* fun getSunrise(): String = sunrise
    fun setSunrise(sunrise: String) {
        this.sunrise = sunrise
    }

    fun getSunset(): String = sunset
    fun setSunset(sunset: String) {
        this.sunset = sunset
    }*/

    fun getIconId(): String = iconId
    fun setIconId(iconId: String) {
        this.iconId = iconId
    }
}
