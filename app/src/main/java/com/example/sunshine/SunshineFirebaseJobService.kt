package com.example.sunshine

import android.os.AsyncTask
import com.example.sunshine.activities.forecast.WeatherRepository
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

open class SunshineFirebaseJobService : JobService() {
    private lateinit var mFetchWeatherTask: FetchWeatherTask

    override fun onStartJob(job: JobParameters): Boolean {
        mFetchWeatherTask.execute()
        jobFinished(job, false)
        return true
    }

    override fun onStopJob(job: JobParameters): Boolean {
        if (!::mFetchWeatherTask.isInitialized) {
            mFetchWeatherTask.cancel(true)
        }
        return true
    }

    class FetchWeatherTask: AsyncTask<Void,Void,Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            WeatherRepository().getWeatherData()
            return null
        }
    }

}