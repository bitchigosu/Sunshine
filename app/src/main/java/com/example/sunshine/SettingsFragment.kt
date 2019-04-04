package com.example.sunshine

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.pref_settings)
        val prefScreen = preferenceScreen
        val sharedPreferences = prefScreen.sharedPreferences
        val count = prefScreen.preferenceCount

        for (i in 0 until count) {
            val p: Preference = prefScreen.getPreference(i)
            val value = sharedPreferences.getString(p.key, "")
            setPreferenceSummary(p, value)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val preference = findPreference(key)
        if (preference != null) {
            val value = sharedPreferences!!.getString(preference.key, "")
            setPreferenceSummary(preference, value)
        }
    }

    private fun setPreferenceSummary(preference: Preference, value: String) {
        if (preference is ListPreference) {
            val listPreference: ListPreference = preference
            val prefIndex = listPreference.findIndexOfValue(value)
            if (prefIndex >= 0) {
                listPreference.summary = listPreference.entries[prefIndex]
            }
        } else if (preference is EditTextPreference) {
            preference.summary = value
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}