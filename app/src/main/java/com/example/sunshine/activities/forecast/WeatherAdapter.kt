package com.example.sunshine.activities.forecast

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sunshine.R
import com.example.sunshine.database.WeatherEntry
import kotlinx.android.synthetic.main.list_item.view.*

class WeatherAdapter(private val listener: (Int) -> Unit, private var weatherData: ArrayList<String>) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    private val TAG = "WeatherAdapter"

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

    fun updateData(data: List<WeatherEntry>) {
        weatherData.clear()
        val list = ArrayList<String>()
        for (i in 0 until data.size) {
            Log.d(TAG, "updateData: ${data.elementAt(i)}")
            list.add(data[i].toString())
        }

        weatherData = list
        notifyDataSetChanged()
    }

    class WeatherViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}