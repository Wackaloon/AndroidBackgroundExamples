package com.wackalooon.androidbackgroundworks.gps

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import androidx.core.app.ActivityCompat.checkSelfPermission
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import java.lang.IllegalStateException
import java.util.*

class LastLocationProvider constructor(private val context: Context) {

    private fun getLocation(callback: (Result<Location>) -> Unit) {
        if (!hasLocationPermission()) {
            callback.invoke(Result.failure(IllegalStateException("Missing location permission")))
            return
        }
        val lastLocationTask = getLocationTask()
        lastLocationTask.addOnCompleteListener { task ->
            val location = task.getLocation()
            if (location != null) {
                callback.invoke(Result.success(location))
            } else {
                callback.invoke(Result.failure(NoSuchElementException("No location found")))
            }
        }
    }

    @SuppressLint("MissingPermission") // permission is checked
    private fun getLocationTask(): Task<Location> {
        return LocationServices
            .getFusedLocationProviderClient(context)
            .lastLocation
    }

    private fun Task<Location>.getLocation(): Location? {
        return if (isSuccessful && result != null) {
            result!!
        } else {
            null
        }
    }

    private fun hasLocationPermission(): Boolean {
        return checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
                || checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED

    }
}
