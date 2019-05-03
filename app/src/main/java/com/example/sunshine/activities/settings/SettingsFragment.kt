package com.example.sunshine.activities.settings

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.sunshine.R
import com.example.sunshine.SuperApplication
import com.example.sunshine.activities.newlocation.NewLocation
import com.example.sunshine.utils.SunshinePreferences
import com.example.sunshine.utils.SunshineSyncUtils

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val TAG = "SettingsFragment"
    private val con = SuperApplication.getContext()

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.pref_settings)
    }

    override fun onResume() {
        super.onResume()
        setPrefs()
    }

    private fun setPrefs() {
        val prefScreen = preferenceScreen
        val sharedPreferences = prefScreen.sharedPreferences
        val count = prefScreen.preferenceCount

        for (i in 0 until count) {
            val p: Preference = prefScreen.getPreference(i)
            if (p is androidx.preference.SwitchPreference) {
                //switchPreference.widgetLayoutResource = R.layout.temperature_units_switch
            } else {
                val value = sharedPreferences.getString(p.key, "")
                setPreferenceSummary(p, value!!)
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val activity: Activity = this.activity!!
        val preference = findPreference(key)

        if (preference != null) {
            if (key.equals(getString(R.string.pref_location_key))) {
                if (sharedPreferences!!.getString(preference.key, "") == "current_location") {
                    SunshineSyncUtils.startImmediateSync(activity)
                } else if (sharedPreferences.getString(preference.key, "") == "other_location") {
                    val intent = Intent(context, NewLocation::class.java)
                    startActivity(intent)
                }
            } else if (key.equals(getString(R.string.pref_units_key))) {
                if (preference is androidx.preference.SwitchPreference) {
                }
            }
            if (preference !is androidx.preference.SwitchPreference) {
                val value = sharedPreferences!!.getString(preference.key, "")
                setPreferenceSummary(preference, value!!)
            }
        }
    }

    private fun setPreferenceSummary(preference: Preference, value: String) {
        if (preference is ListPreference) {
            if (value == "other_location") {
                preference.summary = SunshinePreferences.getPreferredWeatherLocation(con)
            } else {
                val listPreference: ListPreference = preference
                val prefIndex = listPreference.findIndexOfValue(value)
                if (prefIndex >= 0) {
                    listPreference.summary = listPreference.entries[prefIndex]
                }
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