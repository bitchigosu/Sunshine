package com.example.sunshine.activities.forecast

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import com.example.sunshine.R
import com.example.sunshine.SuperApplication
import com.example.sunshine.database.WeatherEntry
import com.example.sunshine.databinding.ListItemBinding
import com.example.sunshine.databinding.ListItemForecastTodayBinding
import com.example.sunshine.utils.JsonUtil
import kotlinx.android.synthetic.main.list_item.view.*

class WeatherAdapter(private val listener: (Int) -> Unit) :
    RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    private val TAG = "WeatherAdapter"
    private var weatherData: List<WeatherEntry> = emptyList()
    private var mUseTodayLayout: Boolean = false

    init {
        mUseTodayLayout = SuperApplication.getContext().resources
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
        if (holder is FutureWeatherViewHolder && weatherData.size > position) {
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
        } else if (holder is TodayWeatherViewHolder && weatherData.size > position) {
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

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class FutureWeatherViewHolder(
        private val parent: ViewGroup,
        private val binding: ListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item,
            parent,
            false
        )
    ) : ViewHolder(binding.root) {


        fun bind(item: WeatherEntry) {
            binding.item = item
        }
    }

    class TodayWeatherViewHolder(
        private val parent: ViewGroup,
        private val binding: ListItemForecastTodayBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item_forecast_today,
            parent,
            false
        )
    ) : ViewHolder(binding.root) {

        fun bind(item: WeatherEntry) {
            binding.item = item
        }
    }
}