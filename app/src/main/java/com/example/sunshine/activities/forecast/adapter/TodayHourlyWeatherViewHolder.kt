package com.example.sunshine.activities.forecast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.sunshine.R
import com.example.sunshine.database.HourlyWeatherEntry
import com.example.sunshine.databinding.ListHourlyBinding

class TodayHourlyWeatherViewHolder(private val parent: ViewGroup,
                                   private val binding: ListHourlyBinding = DataBindingUtil.inflate(
                                       LayoutInflater.from(parent.context),
                                       R.layout.list_hourly,
                                       parent,
                                       false)) : ViewHolder(binding.root) {
    override fun bind(item: Any) {
        binding.item = item as HourlyWeatherEntry
    }
}
