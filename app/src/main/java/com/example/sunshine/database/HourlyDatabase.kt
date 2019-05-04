package com.example.sunshine.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HourlyWeatherEntry::class], version = 1, exportSchema = false)
abstract class HourlyDatabase : RoomDatabase() {
    companion object {
        private const val TAG = "HourlyDatabase"
        private val LOCK = Any()
        private const val DATABASE_NAME = "HourlyWeather"
        private var sInstance: HourlyDatabase? = null

        fun getInstance(context: Context): HourlyDatabase? {
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = Room.databaseBuilder(
                        context.applicationContext,
                        HourlyDatabase::class.java,
                        DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return sInstance
        }

    }

    abstract fun hourlyWeatherDao(): HourlyWeatherDao
}