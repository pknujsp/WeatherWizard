package io.github.pknujsp.weatherwizard.core.common.manager

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class AppLocationManagerImpl(context: Context) : AppLocationManager {
    private val fusedLocationProvider: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override val isGpsProviderEnabled: Boolean
        get() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): AppLocationManager.LocationResult {
        return findCurrentLocation()?.let {
            Log.d("AppLocationManagerImpl", "getCurrentLocation: $it")
            AppLocationManager.LocationResult.Success(it)
        } ?: run {
            Log.d("AppLocationManagerImpl", "getCurrentLocation: null")
            AppLocationManager.LocationResult.Failure
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun findCurrentLocation(): Location? = suspendCoroutine { continuation ->
        fusedLocationProvider.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token).addOnFailureListener {
            continuation.resume(null)
        }.addOnCanceledListener {
            continuation.resume(null)
        }.addOnSuccessListener {
            it?.run {
                continuation.resume(this)
            } ?: run {
                continuation.resume(null)
            }
        }
    }

}

interface AppLocationManager {
    val isGpsProviderEnabled: Boolean
    suspend fun getCurrentLocation(): LocationResult

    sealed interface LocationResult {
        data class Success(val location: Location) : LocationResult
        data object Failure : LocationResult
    }
}