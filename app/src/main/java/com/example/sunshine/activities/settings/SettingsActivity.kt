package com.example.sunshine.activities.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.sunshine.R
import com.example.sunshine.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backImage.setOnClickListener {
            finish()
        }
        binding.poweredByDarksky.apply {
            movementMethod = LinkMovementMethod.getInstance()
            val darkskyUrl = "https://darksky.net/poweredby/"
            val message = SpannableString(getString(R.string.powered_by_darksky)).apply {
                setLinkSpan("DarkSky", darkskyUrl)
            }
            text = message
        }
    }

    private fun SpannableString.setLinkSpan(text: String, url: String) {
        val textIndex = this.indexOf(text)
        setSpan(
            object : ClickableSpan() {
                override fun onClick(widget: View) {
                    Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
                        .also { startActivity(it) }
                }
            },
            textIndex,
            textIndex + text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}
