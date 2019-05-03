package com.example.sunshine.activities.forecast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.sunshine.R
import com.example.sunshine.database.WeatherEntry
import com.example.sunshine.databinding.ListItemBinding

class FutureWeatherViewHolder(
    private val parent: ViewGroup,
    private val binding: ListItemBinding = DataBindingUtil.inflate(
        LayoutInflater.from(parent.context),
        R.layout.list_item,
        parent,
        false)) : ViewHolder(binding.root) {

    override fun bind(item: WeatherEntry) {
        binding.item = item
    }
}