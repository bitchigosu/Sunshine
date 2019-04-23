package com.example.sunshine.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weathers")
class WeatherEntry(
    @PrimaryKey
    private var id: Int,
    private var city: String,
    private var date: String,
    private var weatherDesc: String,
    private var maxTemp: Int,
    private var minTemp: Int,
    private var windSpeed: Double,
    private var pressure: Double,
    private var humidity: Int,
    private var iconId: String
) {

    fun getId(): Int? = id
    fun setId(id: Int) {
        this.id = id
    }

    fun getCity(): String = city
    fun setCity(city: String) {
        this.city = city
    }

    fun getWeatherDesc(): String = weatherDesc
    fun setWeatherDesc(weatherId: String) {
        this.weatherDesc = weatherId
    }

    fun getMaxTemp(): Int = maxTemp
    fun setMaxTemp(maxTemp: Int) {
        this.maxTemp = maxTemp
    }

    fun getWindSpeed(): Double = windSpeed
    fun setWindSpeed(windSpeed: Double) {
        this.windSpeed = windSpeed
    }

    fun getPressure(): Double = pressure
    fun setPressure(pressure: Double) {
        this.pressure = pressure
    }

    fun getMinTemp(): Int = minTemp
    fun setMinTemp(minTemp: Int) {
        this.minTemp = minTemp
    }

    fun getDate(): String = date
    fun setDate(date: String) {
        this.date = date
    }

    fun getHumidity(): Int = humidity
    fun setHumidity(humidity: Int) {
        this.humidity = humidity
    }


    fun getIconId(): String = iconId
    fun setIconId(iconId: String) {
        this.iconId = iconId
    }


}