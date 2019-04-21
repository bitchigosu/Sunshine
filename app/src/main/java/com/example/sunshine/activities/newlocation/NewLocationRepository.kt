package com.example.sunshine.activities.newlocation

import com.example.sunshine.utils.JsonUtil
import okhttp3.*
import java.io.IOException

class NewLocationRepository {

    fun getQueryResults(cityName: String, competition: (list: ArrayList<String>) -> Unit){

        val httpUrl = HttpUrl.parse(SCHEME + GEOCODE_URL + cityName + GEOCODE_API_KEY)
        val client = OkHttpClient()

        client.newCall(makeRequest(httpUrl)).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val jsonResult = response.body()!!.string()
                JsonUtil.getCitiesFromJson(jsonResult).addOnSuccessListener {
                    competition(it)
                }
            }
            override fun onFailure(call: Call, e: IOException) {}
        })
    }

    private fun makeRequest(url: HttpUrl?): Request = Request.Builder()
        .url(url!!)
        .build()

    companion object {
        private const val SCHEME = "https://"
        private const val GEOCODE_URL = "api.opencagedata.com/geocode/v1/json?q="
        private const val GEOCODE_API_KEY = "&key=ee53b979dd30468aaeeb7e5d252625cd"
    }
}