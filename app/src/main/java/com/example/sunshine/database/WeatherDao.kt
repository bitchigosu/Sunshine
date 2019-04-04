package com.example.sunshine.database

import android.arch.persistence.room.*
import java.util.ArrayList

@Dao
interface WeatherDao {

    @Query("SELECT * FROM weathers ORDER BY date")
    fun loadAllWeather(): List<WeatherEntry>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateWeather(weatherEntry: WeatherEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(weatherEntry: WeatherEntry)

    @Delete
    fun deleteWeather(weatherEntry: WeatherEntry)

}