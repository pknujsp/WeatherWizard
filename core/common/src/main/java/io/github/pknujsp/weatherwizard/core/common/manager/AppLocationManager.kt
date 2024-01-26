package io.github.pknujsp.weatherwizard.core.common.manager

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LastLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import io.github.pknujsp.weatherwizard.core.common.util.toCoordinate
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.Duration
import kotlin.coroutines.resume

private class AppLocationManagerImpl(private val context: Context) : AppLocationManager {
    private val fusedLocationProvider: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val maxUpdateAgeMillis = Duration.ofMinutes(30).toMillis()
    private val durationMillis = Duration.ofSeconds(4).toMillis()

    override val isGpsProviderEnabled: Boolean
        get() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    override val isPermissionGranted: Boolean
        get() = context.checkSelfPermission(PermissionType.LOCATION)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): AppLocationManager.LocationResult {
        return findCurrentLocation()?.let {
            AppLocationManager.LocationResult.Success(it.latitude.toCoordinate(), it.longitude.toCoordinate())
        } ?: run {
            getLastLocation()?.let {
                AppLocationManager.LocationResult.Success(it.latitude.toCoordinate(), it.longitude.toCoordinate())
            } ?: run {
                AppLocationManager.LocationResult.Failure
            }
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun findCurrentLocation(): Location? = suspendCancellableCoroutine { continuation ->
        val request = CurrentLocationRequest.Builder().setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .setMaxUpdateAgeMillis(maxUpdateAgeMillis).setDurationMillis(durationMillis).build()
        val cancellationToken = CancellationTokenSource()

        val task = fusedLocationProvider.getCurrentLocation(request, cancellationToken.token)
        task.addOnCompleteListener { result ->
            if (result.isSuccessful) {
                continuation.resume(result.result ?: null)
            } else {
                continuation.resume(null)
            }
        }

        continuation.invokeOnCancellation { cancellationToken.cancel() }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastLocation(): Location? = suspendCancellableCoroutine { continuation ->
        fusedLocationProvider.getLastLocation(LastLocationRequest.Builder().build()).addOnCompleteListener { result ->
            if (result.isSuccessful) {
                continuation.resume(result.result ?: null)
            } else {
                continuation.resume(null)
            }
        }
    }
}

interface AppLocationManager : AppComponentManager {
    companion object : AppComponentManagerInitializer {
        private var instance: AppLocationManager? = null

        override fun getInstance(context: Context): AppLocationManager = synchronized(this) {
            instance ?: AppLocationManagerImpl(context).also { instance = it }
        }
    }

    val isGpsProviderEnabled: Boolean
    val isPermissionGranted: Boolean
    suspend fun getCurrentLocation(): LocationResult

    sealed interface LocationResult {
        data class Success(val latitude: Double, val longitude: Double) : LocationResult
        data object Failure : LocationResult
    }
}