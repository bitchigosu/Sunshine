package com.example.sunshine.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context

@Database(entities = [WeatherEntry::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        private const val TAG = "AppDatabase"
        private val LOCK = Any()
        private const val DATABASE_NAME = "Weather"
        private var sInstance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        AppDatabase.DATABASE_NAME
                    )
                        .build()
                }
            }
            return sInstance
        }

    }

    abstract fun weatherDao(): WeatherDao
}