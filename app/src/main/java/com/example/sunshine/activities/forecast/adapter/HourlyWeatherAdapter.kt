package com.example.sunshine.activities.forecast.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sunshine.database.HourlyWeatherEntry

class HourlyWeatherAdapter : RecyclerView.Adapter<ViewHolder>() {

    private var weatherData: List<HourlyWeatherEntry> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        TodayHourlyWeatherViewHolder(parent)


    override fun getItemCount(): Int = weatherData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (weatherData.size > position) {
            val current = weatherData[position]
            holder.bind(current)
        }
    }

    fun updateData(data: List<HourlyWeatherEntry>) {
        weatherData = data
        notifyDataSetChanged()
    }
}