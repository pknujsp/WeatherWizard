package io.github.pknujsp.weatherwizard.core.common

import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.weatherwizard.core.common.manager.AppLocationManager
import io.github.pknujsp.weatherwizard.core.common.manager.AppNotificationManager
import io.github.pknujsp.weatherwizard.core.common.manager.NotificationType
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : LifecycleService() {

    @Inject lateinit var appLocationManager: AppLocationManager
    private val appNotificationManager by lazy { AppNotificationManager(this) }

    companion object {
        const val ACTION_START_LOCATION_SERVICE = "ACTION_START_LOCATION_SERVICE"
        const val ACTION_STOP_LOCATION_SERVICE = "ACTION_STOP_LOCATION_SERVICE"
        const val SERVICE_ID = 1
    }

    private fun stopLocationService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startLocationService() {
        lifecycleScope.launch {
            startForeground(SERVICE_ID,
                appNotificationManager.createNotification(NotificationType.LOCATION_SERVICE, applicationContext).build())
            val location = appLocationManager.getCurrentLocation()

            when (location) {
                is AppLocationManager.LocationResult.Success -> {
                }

                is AppLocationManager.LocationResult.Failure -> {

                }
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.run {
            when (action) {
                ACTION_START_LOCATION_SERVICE -> startLocationService()
                ACTION_STOP_LOCATION_SERVICE -> stopLocationService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}