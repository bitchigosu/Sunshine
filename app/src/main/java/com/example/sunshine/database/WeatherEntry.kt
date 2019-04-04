package com.example.sunshine.database

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "weathers")
class WeatherEntry(
    @PrimaryKey(autoGenerate = true) private var id: Int,
    private var city: String,
    private var date: Date,
    private var weatherDesc: String,
    private var maxTemp: Double,
    private var minTemp: Double,
    private var windSpeed: Double,
    private var pressure: Double
) {

    @Ignore
    constructor(
        city: String,
        date: Date,
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
    )

    fun getId(): Int = id
    public fun setId(id: Int) {
        this.id = id
    }

    public fun getCity(): String = city
    public fun setCity(city: String) {
        this.city = city
    }

    public fun getWeatherDesc(): String = weatherDesc
    public fun setWeatherDesc(weatherId: String) {
        this.weatherDesc = weatherId
    }

    public fun getMaxTemp(): Double = maxTemp
    public fun setMaxTemp(maxTemp: Double) {
        this.maxTemp = maxTemp
    }

    public fun getWindSpeed(): Double = windSpeed
    public fun setWindSpeed(windSpeed: Double) {
        this.windSpeed = windSpeed
    }

    public fun getPressure(): Double = pressure
    public fun setPressure(pressure: Double) {
        this.pressure = pressure
    }

    public fun getMinTemp(): Double = minTemp
    public fun setMinTemp(minTemp: Double) {
        this.minTemp = minTemp
    }

    public fun getDate(): Date = date
    public fun setDate(date: Date) {
        this.date = date
    }


}