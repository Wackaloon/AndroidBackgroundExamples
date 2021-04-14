package com.wackalooon.androidbackgroundworks.gps

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.os.Looper
import androidx.core.app.ActivityCompat.checkSelfPermission
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import java.util.*

/**
 * The desired interval for location updates. Inexact. Updates may be more or less frequent.
 */
private const val UPDATE_INTERVAL_IN_MILLISECONDS = 60_000L

/**
 * The fastest rate for active location updates. Updates will never be more frequent
 * than this value.
 */
private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 10_000L

class PeriodicBackgroundLocationProvider constructor(private val context: Context) {

    /**
     * @throws IllegalStateException when has no permission for background location
     */
    fun startLocationUpdates() {
        if (!hasLocationPermission()) {
            throw IllegalStateException("Missing location permission")
        }
        val locationRequest = createLocationRequest()
        subscribeToLocationUpdates(locationRequest)
    }

    fun stopLocationUpdates() {
        unsubscribeToLocationUpdates()
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create()
            .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    @SuppressLint("MissingPermission") // permission is checked
    private fun subscribeToLocationUpdates(request: LocationRequest) {
        LocationServices
            .getFusedLocationProviderClient(context)
            .requestLocationUpdates(request, getPendingIntent())
    }

    private fun getPendingIntent(): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            0,
            LocationBroadcastReceiver.createIntent(context),
            0
        )
    }

    private fun unsubscribeToLocationUpdates() {
        LocationServices
            .getFusedLocationProviderClient(context)
            .removeLocationUpdates(getPendingIntent())
    }

    private fun hasLocationPermission(): Boolean {
        val backgroundLocation = if (SDK_INT >= VERSION_CODES.Q) {
            checkSelfPermission(context, ACCESS_BACKGROUND_LOCATION) == PERMISSION_GRANTED
        } else {
            true
        }
        return (checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
                || checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED)
                && backgroundLocation

    }
}
