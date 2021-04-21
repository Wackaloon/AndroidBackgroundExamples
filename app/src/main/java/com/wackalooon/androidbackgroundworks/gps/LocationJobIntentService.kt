package com.wackalooon.androidbackgroundworks.gps

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService

private const val JOB_ID = 142

class LocationJobIntentService : JobIntentService() {

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            JobIntentService.enqueueWork(
                context,
                LocationJobIntentService::class.java,
                JOB_ID,
                intent)
        }
    }

    override fun onHandleWork(intent: Intent) {

    }
}
