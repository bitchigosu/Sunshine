package com.example.sunshine.database

import androidx.room.*
import androidx.lifecycle.LiveData


@Dao
interface WeatherDao {

    @Query("SELECT * FROM weathers")
    fun getAllWeather(): LiveData<List<WeatherEntry>>

    @Query("SELECT COUNT(*) FROM weathers")
    fun getRowCount(): Int

    @Query("SELECT * FROM weathers WHERE id=:id")
    fun getWeatherById(id: Int): LiveData<WeatherEntry>

    @Query("DELETE FROM weathers")
    fun deleteAll()

    @Query("UPDATE weathers SET minTemp=:minTemp, maxTemp=:maxTemp WHERE id=:id")
    fun updateTemperatureValues(minTemp: Int, maxTemp: Int, id: Int)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateWeather(weatherEntry: WeatherEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(weatherEntry: WeatherEntry)
}