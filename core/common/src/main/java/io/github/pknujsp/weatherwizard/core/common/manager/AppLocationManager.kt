package io.github.pknujsp.weatherwizard.core.common.manager

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class AppLocationManagerImpl(context: Context) : AppLocationManager {
    private val fusedLocationProvider: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val requestingLocationUpdates = AtomicBoolean(false)
    private var callback: LocationCallback? = null

    override val isGpsProviderEnabled: Boolean
        get() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): AppLocationManager.LocationResult {
        var result = suspendCoroutine { continuation ->
            fusedLocationProvider.lastLocation.addOnCompleteListener { task ->
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
            result = findCurrentLocation()?.run {
                Result.success(this)
            } ?: Result.failure(Throwable("Location is null"))
        }

        return if (result.isSuccess) {
            AppLocationManager.LocationResult.Success(result.getOrThrow())
        } else {
            AppLocationManager.LocationResult.Failure(result.exceptionOrNull()!!)
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun findCurrentLocation(): Location? = suspendCoroutine { continuation ->
        if (requestingLocationUpdates.get()) {
            callback?.run {
                fusedLocationProvider.removeLocationUpdates(this)
            }
        } else {
            requestingLocationUpdates.set(true)
        }

        callback = object : LocationCallback() {
            val resumed = AtomicBoolean(false)

            override fun onLocationResult(p: LocationResult) {
                if (!resumed.get()) {
                    resumed.set(true)
                    requestingLocationUpdates.set(false)
                    fusedLocationProvider.removeLocationUpdates(this)
                    continuation.resume(p.lastLocation)
                }
            }
        }

        fusedLocationProvider.requestLocationUpdates(LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 200L).build(),
            callback!!,
            Looper.getMainLooper())
    }


}

interface AppLocationManager {
    val isGpsProviderEnabled: Boolean
    suspend fun getCurrentLocation(): LocationResult

    sealed interface LocationResult {
        data class Success(val location: Location) :
            LocationResult

        data class Failure(val throwable: Throwable) :
            LocationResult
    }
}