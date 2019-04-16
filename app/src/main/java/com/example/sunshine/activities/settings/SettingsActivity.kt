package com.example.sunshine.activities.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.sunshine.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        back_image.setOnClickListener {
            finish()
        }

    }
}
