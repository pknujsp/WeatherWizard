package io.github.pknujsp.weatherwizard.core.common.manager

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class AppLocationManagerImpl(context: Context) : AppLocationManager {
    private val fusedLocationProvider: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val isRequestedLocationUpdate = AtomicBoolean(false)
    private var callback: LocationCallback? = null

    override val isGpsProviderEnabled: Boolean
        get() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): AppLocationManager.LocationResult {
        return findCurrentLocation()?.let {
            AppLocationManager.LocationResult.Success(it)
        } ?: run {
            AppLocationManager.LocationResult.Failure(Throwable("Location is null"))
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun findCurrentLocation(): Location? = suspendCoroutine { continuation ->
        fusedLocationProvider.getCurrentLocation(createCurrentLocationRequest(), object : CancellationToken() {
            override fun isCancellationRequested(): Boolean = false
            override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken = CancellationTokenSource().token
        }).addOnFailureListener {
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

    private fun createCurrentLocationRequest() =
        CurrentLocationRequest.Builder().setDurationMillis(6_000L).setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()
}

interface AppLocationManager {
    val isGpsProviderEnabled: Boolean
    suspend fun getCurrentLocation(): LocationResult

    sealed interface LocationResult {
        data class Success(val location: Location) : LocationResult

        data class Failure(val throwable: Throwable) : LocationResult
    }
}