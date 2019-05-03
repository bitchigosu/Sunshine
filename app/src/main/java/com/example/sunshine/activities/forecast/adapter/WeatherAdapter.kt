package com.example.sunshine.activities.forecast.adapter

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sunshine.R
import com.example.sunshine.SuperApplication
import com.example.sunshine.database.WeatherEntry
import com.example.sunshine.utils.JsonUtil
import kotlinx.android.synthetic.main.list_item.view.*

class WeatherAdapter(private val listener: (Int) -> Unit) :
    RecyclerView.Adapter<ViewHolder>() {

    private val TAG = "WeatherAdapter"
    private var weatherData: List<WeatherEntry> = emptyList()
    private var mUseTodayLayout: Boolean = false

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
                forecast_icon.setImageDrawable(
                    ContextCompat.getDrawable(
                        SuperApplication.getContext(),
                        JsonUtil.getIcon(current.getIconId())
                    )
                )
                setOnClickListener { listener(position) }
            }
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

    init {
        mUseTodayLayout = SuperApplication.getContext().resources
            .getBoolean(R.bool.use_today_layout)
    }

    companion object {
        const val VIEW_TYPE_TODAY = 0
        const val VIEW_TYPE_FUTURE_DAY = 1

        @JvmStatic
        @BindingAdapter("items")
        fun RecyclerView.bindItems(items: List<WeatherEntry>?) {
            if (items != null) {
                val adapter = adapter as WeatherAdapter
                adapter.updateData(items)
            }
        }
    }

}