package com.example.sunshine.database

import android.arch.persistence.room.*
import android.arch.lifecycle.LiveData



@Dao
interface WeatherDao {

    @Query("SELECT * FROM weathers")
    fun getAllWeather():  LiveData<List<WeatherEntry>>

    @Query("SELECT COUNT(*) FROM weathers")
    fun getRowCount(): Int

    @Query("SELECT * FROM weathers WHERE id=:id")
    fun getWeatherById(id: Int): WeatherEntry

    @Query("DELETE FROM weathers")
    fun deleteAll()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateWeather(weatherEntry: WeatherEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(weatherEntry: WeatherEntry)
}