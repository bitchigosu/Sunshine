package com.example.sunshine.activities.forecast.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sunshine.SuperApplication
import com.example.sunshine.database.HourlyWeatherEntry
import com.example.sunshine.utils.JsonUtil
import kotlinx.android.synthetic.main.list_hourly.view.*

class HourlyWeatherAdapter : RecyclerView.Adapter<ViewHolder>() {

    private val TAG = "WeatherTodayHourly"
    private var weatherData: List<HourlyWeatherEntry> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        TodayHourlyWeatherViewHolder(parent)


    override fun getItemCount(): Int = weatherData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (weatherData.size > position) {
            val current = weatherData[position]
            holder.bind(current)
            with(holder.itemView) {
                icon_today.setImageDrawable(
                    ContextCompat.getDrawable(
                        SuperApplication.getContext(),
                        JsonUtil.getIcon(current.getIconId())
                    )
                )
            }
        }
    }

    fun updateData(data: List<HourlyWeatherEntry>) {
        weatherData = data
        notifyDataSetChanged()
    }

    companion object {

        @JvmStatic
        @BindingAdapter("itemsToday")
        fun RecyclerView.bindItems(items: List<HourlyWeatherEntry>?) {
            if (items != null) {
                val adapter = adapter as HourlyWeatherAdapter
                adapter.updateData(items)
            }
        }
    }
}