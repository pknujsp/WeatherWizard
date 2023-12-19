package io.github.pknujsp.weatherwizard.core.common.manager

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.app.PendingIntentCompat.send
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class AppLocationManagerImpl(context: Context) : AppLocationManager {
    private val fusedLocationProvider: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override val isGpsProviderEnabled: Boolean
        get() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): AppLocationManager.LocationResult {
        Log.d("AppLocationManagerImpl", "getCurrentLocation")
        return findCurrentLocation().first()?.let {
            Log.d("AppLocationManagerImpl", "getCurrentLocation Result: $it")
            AppLocationManager.LocationResult.Success(it)
        } ?: run {
            Log.d("AppLocationManagerImpl", "getCurrentLocation Result: null")
            AppLocationManager.LocationResult.Failure
        }
    }

    @SuppressLint("MissingPermission")
    private fun findCurrentLocation() = callbackFlow {
        fusedLocationProvider.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnFailureListener {
                trySend(null)
            }.addOnCanceledListener {
                trySend(null)
            }.addOnSuccessListener {
                trySend(it ?: null)
            }
        awaitClose { }
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