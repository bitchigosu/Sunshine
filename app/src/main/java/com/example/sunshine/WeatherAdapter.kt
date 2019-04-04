package com.example.sunshine

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item.view.*

class WeatherAdapter(private val listener: (Int) -> Unit, private val weatherData: MutableList<String>) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return WeatherViewHolder(view)
    }

    override fun getItemCount(): Int = weatherData.size

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        with(holder.view) {
            textNumbers.text = weatherData[position]
            setOnClickListener { listener(position) }
        }
    }

    class WeatherViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}