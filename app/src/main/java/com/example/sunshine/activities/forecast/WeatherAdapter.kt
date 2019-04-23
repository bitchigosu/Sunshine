package com.example.sunshine.activities.forecast

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sunshine.R
import com.example.sunshine.SuperApplication
import com.example.sunshine.database.WeatherEntry
import com.example.sunshine.utils.JsonUtil
import kotlinx.android.synthetic.main.list_item.view.*

class WeatherAdapter(private val listener: (Int) -> Unit) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    private val TAG = "WeatherAdapter"
    private lateinit var weatherData: List<WeatherEntry>
    private var mUseTodayLayout: Boolean = false

    init {
        mUseTodayLayout = SuperApplication.getContext().resources
            .getBoolean(R.bool.use_today_layout)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        var layoutId = 0
        when (viewType) {
            VIEW_TYPE_TODAY -> {
                layoutId = R.layout.list_item_forecast_today
            }
            VIEW_TYPE_FUTURE_DAY -> {
                layoutId = R.layout.list_item
            }
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return WeatherViewHolder(view)
    }

    override fun getItemCount(): Int = if (!::weatherData.isInitialized) 0 else weatherData.size

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        with(holder.view) {
            val current = weatherData[position]
            forecast_icon.setImageDrawable(
                ContextCompat.getDrawable(
                    SuperApplication.getContext(),
                    JsonUtil.getIcon(current.getIconId())
                )
            )
            day_text.text = current.getDate()
            weather_description_text.text = current.getWeatherDesc()
            max_temp_text.text = current.getMaxTemp().toString() + "°"
            min_temp_text.text = current.getMinTemp().toString() + "°"
            setOnClickListener { listener(position) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (mUseTodayLayout && position == 0)
            VIEW_TYPE_TODAY
        else
            VIEW_TYPE_FUTURE_DAY
    }

    fun updateData(data: List<WeatherEntry>) {
        weatherData = data
        notifyDataSetChanged()
    }

    class WeatherViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    companion object {
        const val VIEW_TYPE_TODAY = 0
        const val VIEW_TYPE_FUTURE_DAY = 1
    }
}