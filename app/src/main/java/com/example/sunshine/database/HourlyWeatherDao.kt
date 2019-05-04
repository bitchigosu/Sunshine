package com.example.sunshine.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HourlyWeatherDao {

    @Query("SELECT * FROM hourlyWeather")
    fun getHourlyWeather(): LiveData<List<HourlyWeatherEntry>>

    @Query("DELETE FROM hourlyWeather")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM hourlyWeather")
    fun getRowCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(weatherEntry: HourlyWeatherEntry)
}