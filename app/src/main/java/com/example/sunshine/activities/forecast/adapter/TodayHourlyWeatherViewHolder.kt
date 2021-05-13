package com.example.sunshine.activities.forecast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.sunshine.SuperApplication
import com.example.sunshine.database.HourlyWeatherEntry
import com.example.sunshine.databinding.ListHourlyBinding
import com.example.sunshine.utils.JsonUtil

class TodayHourlyWeatherViewHolder(
    private val parent: ViewGroup,
    private val binding: ListHourlyBinding = ListHourlyBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    )
) : ViewHolder(binding.root) {
    override fun bind(item: Any) {
        (item as HourlyWeatherEntry).let {
            binding.hourValue.text = it.getTime()
            binding.temperature.text = it.getTemperature().toString()
            binding.iconToday.setImageDrawable(
                ContextCompat.getDrawable(
                    SuperApplication.getContext(),
                    JsonUtil.getIcon(it.getIconId())
                )
            )
        }
    }
}
