package com.example.sunshine.activities.forecast.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: Any)
}