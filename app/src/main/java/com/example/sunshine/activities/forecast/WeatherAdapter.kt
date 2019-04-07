package com.example.sunshine.activities.forecast

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sunshine.R
import com.example.sunshine.database.WeatherEntry
import kotlinx.android.synthetic.main.list_item.view.*

class WeatherAdapter(private val listener: (Int) -> Unit) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    private val TAG = "WeatherAdapter"
    private lateinit var weatherData: List<WeatherEntry>

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return WeatherViewHolder(view)
    }

    override fun getItemCount(): Int = if (!::weatherData.isInitialized) 0 else weatherData.size

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        with(holder.view) {
            val current = weatherData[position]
            textNumbers.text = current.getCity() + current.getDate() + current.getWeatherDesc()
            setOnClickListener { listener(position) }
        }
    }

    fun updateData(data: List<WeatherEntry>) {
        weatherData = data
        notifyDataSetChanged()
    }

    class WeatherViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}