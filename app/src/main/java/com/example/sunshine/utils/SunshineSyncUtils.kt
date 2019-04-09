package com.example.sunshine.utils

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import com.example.sunshine.SunshineFirebaseJobService
import com.example.sunshine.activities.forecast.SunshineSyncIntentService
import com.example.sunshine.mWeatherDao
import com.firebase.jobdispatcher.*
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit


class SunshineSyncUtils {
    companion object {
        private val SYNC_INTERVAL_HOURS = 3
        private val SYNC_INTERVAL_SECONDS = TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS.toLong()).toInt()
        private val SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3
        private var sInitialized: Boolean = false

        @Synchronized
        fun initialize(context: Context) {
            if (sInitialized)
                return
            else {
                sInitialized = true
                scheduleFirebaseJobDispatcherSync(context)
                RowsCount(context)
            }
        }

        internal fun startImmediateSync(context: Context?) {
            val intent = Intent(
                context,
                SunshineSyncIntentService::class.java
            )
            context?.startService(intent)
        }

        private class RowsCount(context: Context) : AsyncTask<Void, Void, Void>() {
            private val activityReference: WeakReference<Context> = WeakReference(context)
            override fun doInBackground(vararg params: Void?): Void? {
                if (mWeatherDao.getRowCount() == 0) {
                    val activity = activityReference.get()
                    startImmediateSync(activity)
                }
                return null
            }
        }

        private fun scheduleFirebaseJobDispatcherSync(context: Context) {
            val driver = GooglePlayDriver(context)
            val dispatcher = FirebaseJobDispatcher(driver)
            val syncSunshineJob = dispatcher.newJobBuilder()
                .setService(SunshineFirebaseJobService::class.java)
                .setTag("sunshine-sync")
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(
                    Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS
                    )
                )
                .setReplaceCurrent(true)
                .build()

            dispatcher.schedule(syncSunshineJob)
        }
    }
}