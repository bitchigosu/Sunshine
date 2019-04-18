package com.example.sunshine

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.sunshine.utils.Pref
import kotlinx.android.synthetic.main.activity_new_location.*

class NewLocation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_location)

        back_image.setOnClickListener { finish() }
        confirm_image.setOnClickListener {
            val text = new_city_edit_text.text.trim().toString()
            if (text != "")
                Pref.setString(
                    "City"
                    , new_city_edit_text.text.toString()
                )
            finish()
        }
    }
}
