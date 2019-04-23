package com.example.sunshine.activities.newlocation

import com.example.sunshine.utils.JsonUtil
import com.example.sunshine.utils.makeNewCall
import okhttp3.HttpUrl
import okhttp3.OkHttpClient

class NewLocationRepository {

    fun getQueryResults(cityName: String, competition: (list: ArrayList<String>) -> Unit){

        val httpUrl = HttpUrl.parse(SCHEME + GEOCODE_URL + cityName + GEOCODE_API_KEY)
        val client = OkHttpClient()

        client.makeNewCall(httpUrl) {
            JsonUtil.getCitiesFromJson(it).addOnSuccessListener {
                competition(it)
            }
        }
    }

    companion object {
        private const val SCHEME = "https://"
        private const val GEOCODE_URL = "api.opencagedata.com/geocode/v1/json?q="
        private const val GEOCODE_API_KEY = "&key=ee53b979dd30468aaeeb7e5d252625cd"
    }
}