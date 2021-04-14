package com.wackalooon.androidbackgroundworks.gps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Redirects location updates to the job intent service that will handle them.
 */
class LocationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }
        intent.setClass(context, LocationJobIntentService::class.java)
        LocationJobIntentService.enqueueWork(context, intent)
    }
}
