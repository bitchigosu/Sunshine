package com.example.sunshine.activities.newlocation

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable


open class AutoSuggestAdapter(context: Context, resource: Int) : ArrayAdapter<String>(context, resource), Filterable{
    private val listData: MutableList<String>

    init {
        listData = ArrayList()
    }

    fun setData(list: List<String>) {
        listData.clear()
        listData.addAll(list)
    }

    override fun getCount(): Int {
        return listData.size
    }

    override fun getItem(position: Int): String? {
        return listData[position]
    }

    /**
     * Used to Return the full object directly from adapter.
     *
     * @param position
     * @return
     */
    fun getObject(position: Int): String {
        return listData[position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    filterResults.values = listData
                    filterResults.count = listData.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }
        }
    }
}