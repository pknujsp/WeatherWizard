package io.github.pknujsp.weatherwizard.core.common.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AppLocationManager private constructor(context: Context) {
    private val fusedLocationProvider: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val requestingLocationUpdates = AtomicBoolean(false)
    private var callback: LocationCallback? = null

    companion object {
        private var instance: AppLocationManager? = null

        fun getInstance(context: Context): AppLocationManager {
            if (instance == null) {
                instance = AppLocationManager(context)
            }
            return instance!!
        }
    }

    fun isGpsProviderEnabled(): Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): CurrentLocationResult {
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
            CurrentLocationResult.Success(result.getOrThrow())
        } else {
            CurrentLocationResult.Failure(result.exceptionOrNull()!!)
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

    @Composable
    fun OpenSettingsForLocation(onReturnedFromSettings: () -> Unit) {
        val onReturnedFromSettingsState by rememberUpdatedState(onReturnedFromSettings)

        val settingsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onReturnedFromSettingsState()
        }

        LaunchedEffect(Unit) {
            settingsLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    sealed interface CurrentLocationResult {
        data class Success(val location: Location) : CurrentLocationResult
        data class Failure(val throwable: Throwable) : CurrentLocationResult
    }
}