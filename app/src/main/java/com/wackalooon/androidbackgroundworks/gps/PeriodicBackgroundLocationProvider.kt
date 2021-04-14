package com.wackalooon.androidbackgroundworks.gps

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
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
private const val UPDATE_INTERVAL_IN_MILLISECONDS = 10_000L

/**
 * The fastest rate for active location updates. Updates will never be more frequent
 * than this value.
 */
private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 2_000L

class PeriodicBackgroundLocationProvider constructor(private val context: Context) {

    private var locationCallback: LocationCallback? = null

    fun startLocationUpdates(callback: (Result<Location>) -> Unit) {
        if (!hasLocationPermission()) {
            callback.invoke(Result.failure(IllegalStateException("Missing location permission")))
            return
        }
        locationCallback = createLocationCallback(callback)
        val locationRequest = createLocationRequest()
        subscribeToLocationUpdates(locationCallback!!, locationRequest)
    }

    fun stopLocationUpdates() {
        if (locationCallback == null) {
            return
        }
        unsubscribeToLocationUpdates(locationCallback!!)
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create()
            .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    private fun createLocationCallback(callback: (Result<Location>) -> Unit): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                val location = locationResult?.lastLocation
                if (location != null) {
                    callback.invoke(Result.success(location))
                } else {
                    callback.invoke(Result.failure(NoSuchElementException("No location found")))
                }
            }
        }
    }

    @SuppressLint("MissingPermission") // permission is checked
    private fun subscribeToLocationUpdates(callback: LocationCallback, request: LocationRequest) {
        LocationServices
            .getFusedLocationProviderClient(context)
            .requestLocationUpdates(request, PendingIntent.getBroadcast())
    }

    private fun unsubscribeToLocationUpdates(callback: LocationCallback) {
        LocationServices
            .getFusedLocationProviderClient(context)
            .removeLocationUpdates(callback)
    }

    private fun hasLocationPermission(): Boolean {
        return checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
                || checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED

    }
}
