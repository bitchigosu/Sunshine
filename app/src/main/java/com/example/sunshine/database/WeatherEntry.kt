package com.example.sunshine.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

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
    private var pressure: Double
) {

/*    @Ignore
    constructor(
        city: String,
        date: String,
        weatherDesc: String,
        maxTemp: Double,
        minTemp: Double,
        windSpeed: Double,
        pressure: Double
    ) : this(
        id = -1,
        city = city,
        date = date,
        weatherDesc = weatherDesc,
        maxTemp = maxTemp,
        minTemp = minTemp,
        windSpeed = windSpeed,
        pressure = pressure
    )*/

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


}