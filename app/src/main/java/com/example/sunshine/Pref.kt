package com.example.sunshine

import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager
import android.R.id.edit
import android.content.Context


class Pref {
    companion object {
        private val TAG = "Pref"
        private lateinit var prefs: SharedPreferences

        private fun initializePrefs() {
            if (!::prefs.isInitialized) {
                if (SuperApplication.getContext() != null) {
                    prefs = PreferenceManager
                        .getDefaultSharedPreferences(SuperApplication.getContext())
                }
            }
        }

        fun registerListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            initializePrefs()
            prefs.registerOnSharedPreferenceChangeListener(listener)
        }

        fun unregisterListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
            initializePrefs()
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }

        fun getInstance(): SharedPreferences {
            initializePrefs()
            return prefs
        }

        fun getBooleanDefaultFalse(pref: String): Boolean {
            initializePrefs()
            return ::prefs.isInitialized && prefs.getBoolean(pref, false)
        }

        fun getBoolean(pref: String, def: Boolean): Boolean {
            initializePrefs()
            return (::prefs.isInitialized) and (prefs.getBoolean(pref, def))
        }

        fun setBoolean(pref: String, lng: Boolean): Boolean {
            initializePrefs()
            if (::prefs.isInitialized) {
                prefs.edit().putBoolean(pref, lng).apply()
                return true
            }
            return false
        }

        fun toggleBoolean(pref: String) {
            initializePrefs()
            if (::prefs.isInitialized) prefs.edit().putBoolean(pref, !prefs.getBoolean(pref, false)).apply()
        }


        // strings
        fun getStringDefaultBlank(pref: String): String {
            initializePrefs()
            return if (::prefs.isInitialized) {
                prefs.getString(pref, "")
            } else ""
        }

        fun getString(pref: String, def: String): String {
            initializePrefs()
            return if (::prefs.isInitialized) {
                prefs.getString(pref, def)
            } else ""
        }

        fun setString(pref: String, str: String): Boolean {
            initializePrefs()
            if (::prefs.isInitialized) {
                prefs.edit().putString(pref, str).apply()
                return true
            }
            return false
        }


        // numbers
        fun getLong(pref: String, def: Long): Long {
            initializePrefs()
            return if (::prefs.isInitialized) {
                prefs.getLong(pref, def)
            } else def
        }

        fun setLong(pref: String, lng: Long): Boolean {
            initializePrefs()
            if (::prefs.isInitialized) {
                prefs.edit().putLong(pref, lng).apply()
                return true
            }
            return false
        }

        fun getInt(pref: String, def: Int): Int {
            initializePrefs()
            return if (::prefs.isInitialized) {
                prefs.getInt(pref, def)
            } else def
        }

        fun getStringToInt(pref: String, defaultValue: Int): Int {
            try {
                return Integer.parseInt(getString(pref, Integer.toString(defaultValue)))
            } catch (e: Exception) {
                return defaultValue
            }

        }

        fun setInt(pref: String, num: Int): Boolean {
            initializePrefs()
            if (::prefs.isInitialized) {
                prefs.edit().putInt(pref, num).apply()
                return true
            }
            return false
        }


        // misc
        fun removeItem(pref: String): Boolean {
            initializePrefs()
            if (::prefs.isInitialized) {
                prefs.edit().remove(pref).apply()
                return true
            }
            return false
        }
    }
}