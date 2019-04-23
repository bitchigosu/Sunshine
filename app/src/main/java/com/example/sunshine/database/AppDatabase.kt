package com.example.sunshine.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [WeatherEntry::class], version = 7, exportSchema = false)
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
                        DATABASE_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return sInstance
        }

    }

    abstract fun weatherDao(): WeatherDao
}