package com.example.sunshine.activities.newlocation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.sunshine.ViewModelFactory
import com.example.sunshine.databinding.ActivityNewLocationBinding
import com.example.sunshine.utils.Pref
import com.example.sunshine.utils.SunshineSyncUtils

class NewLocation : AppCompatActivity() {

    private lateinit var viewModel: NewLocationViewModel
    private lateinit var binding: ActivityNewLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = AutoSuggestAdapter(this, android.R.layout.simple_dropdown_item_1line)
        binding.newCityEditText.threshold = 2
        binding.newCityEditText.setAdapter(adapter)
        binding.newCityEditText.doOnTextChanged { text, start, before, count ->
            viewModel.searchCityByName(text?.trim().toString())
        }
        binding.newCityEditText.setOnItemClickListener { _, _, position, _ ->
            Pref.setString("City", adapter.getObject(position))
            SunshineSyncUtils.startImmediateSync(this)
            finish()
        }

        binding.backImage.setOnClickListener { finish() }

        viewModel = ViewModelProvider(this, ViewModelFactory())
            .get(NewLocationViewModel::class.java)
        viewModel.cities.observe(this) {
            it?.let {
                adapter.setData(it)
                adapter.notifyDataSetChanged()
            }
        }
    }
}
