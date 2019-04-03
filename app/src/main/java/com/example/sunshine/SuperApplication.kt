package com.example.sunshine

import android.app.Application
import android.content.Context

class SuperApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance : Application
        fun getContext() : Context = instance.applicationContext
    }
}