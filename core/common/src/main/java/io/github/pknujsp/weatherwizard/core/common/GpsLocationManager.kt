package io.github.pknujsp.weatherwizard.core.common

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GpsLocationManager(context: Context) {
    private companion object {
        var fusedLocationProvider: FusedLocationProviderClient? = null
        var requestingLocationUpdates: Boolean = false
        var callback: LocationCallback? = null
    }

    init {
        if (fusedLocationProvider == null) {
            fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context)
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): CurrentLocationResult {
        var result = suspendCoroutine { continuation ->
            fusedLocationProvider?.lastLocation?.addOnCompleteListener { task ->
                val result = if (task.isSuccessful) {
                    task.result?.run {
                        Result.success(this)
                    } ?: Result.failure(Throwable("Location is null"))
                } else {
                    Result.failure(task.exception!!)
                }
                continuation.resume(result)
            }
        }
        if (result.isFailure) {
            result = findCurrentLocation()
        }

        return if (result.isSuccess) {
            CurrentLocationResult.Success(result.getOrThrow())
        } else {
            CurrentLocationResult.Failure(result.exceptionOrNull()!!)
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun findCurrentLocation(): Result<Location> = suspendCoroutine { continuation ->
        if (requestingLocationUpdates) {
            callback?.run {
                fusedLocationProvider?.removeLocationUpdates(this)
            }
        }

        callback = object : LocationCallback() {
            override fun onLocationResult(p: LocationResult) {
                fusedLocationProvider?.removeLocationUpdates(this)
                p.lastLocation?.let { continuation.resume(Result.success(it)) } ?: kotlin.run {
                    continuation.resumeWith(Result.failure(Throwable("Location is null")))
                }
            }
        }

        fusedLocationProvider?.requestLocationUpdates(LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L).build(),
            callback!!,
            Looper.getMainLooper())
    }

    sealed interface CurrentLocationResult {
        data class Success(val location: Location) : CurrentLocationResult
        data class Failure(val throwable: Throwable) : CurrentLocationResult
    }
}