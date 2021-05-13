package com.example.sunshine.activities.forecast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.sunshine.SuperApplication
import com.example.sunshine.database.WeatherEntry
import com.example.sunshine.databinding.ListItemBinding
import com.example.sunshine.utils.JsonUtil

class FutureWeatherViewHolder(
    private val parent: ViewGroup,
    private val binding: ListItemBinding = ListItemBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    )
) : ViewHolder(binding.root) {

    override fun bind(item: Any) {
        (item as WeatherEntry).let {
            binding.dayText.text = it.getDate()
            binding.weatherDescriptionText.text = it.getWeatherDesc()
            binding.minTempText.text = it.getMinTemp().toString()
            binding.maxTempText.text = it.getMaxTemp().toString()
            binding.forecastIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    SuperApplication.getContext(),
                    JsonUtil.getIcon(it.getIconId())
                )
            )
        }
    }
}