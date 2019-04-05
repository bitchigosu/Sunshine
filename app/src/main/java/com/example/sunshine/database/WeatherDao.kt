package com.example.sunshine.database

import android.arch.persistence.room.*
import android.arch.lifecycle.LiveData



@Dao
interface WeatherDao {

    @Query("SELECT * FROM weathers")
    fun loadAllWeather(): List<WeatherEntry>

    @Query("SELECT COUNT(*) FROM weathers")
    fun getRowCount(): Int

    @Query("DELETE FROM weathers")
    fun delete()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateWeather(weatherEntry: WeatherEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(weatherEntry: WeatherEntry)

    @Delete
    fun deleteWeather(weatherEntry: WeatherEntry)

}