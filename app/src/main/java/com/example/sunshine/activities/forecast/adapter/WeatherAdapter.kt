package com.example.sunshine.activities.forecast.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sunshine.R
import com.example.sunshine.SuperApplication
import com.example.sunshine.database.WeatherEntry

class WeatherAdapter(private val listener: (Int) -> Unit) :
    RecyclerView.Adapter<ViewHolder>() {

    private var weatherData: List<WeatherEntry> = emptyList()
    private var useTodayLayout: Boolean = false

    init {
        useTodayLayout = SuperApplication.getContext().resources
            .getBoolean(R.bool.use_today_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_TODAY -> {
                TodayWeatherViewHolder(parent)
            }
            else -> {
                FutureWeatherViewHolder(parent)
            }
        }
    }

    override fun getItemCount(): Int = weatherData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (weatherData.size > position) {
            val current = weatherData[position]
            holder.bind(current)
            with(holder.itemView) {
                setOnClickListener { listener(position) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (useTodayLayout && position == 0)
            VIEW_TYPE_TODAY
        else
            VIEW_TYPE_FUTURE_DAY
    }

    fun updateData(data: List<WeatherEntry>) {
        weatherData = data
        notifyDataSetChanged()
    }

    companion object {
        const val VIEW_TYPE_TODAY = 0
        const val VIEW_TYPE_FUTURE_DAY = 1
    }

}