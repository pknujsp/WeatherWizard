package io.github.pknujsp.weatherwizard.feature.notification.ongoing.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import io.github.pknujsp.weatherwizard.core.common.manager.ServiceManager


class OngoingNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != null && !ServiceManager.isServiceRunning(OngoingNotificationService)) {
            val locationServiceIntent = Intent(context, OngoingNotificationService::class.java).apply {
                action = OngoingNotificationService.ACTION_START_LOCATION_SERVICE
            }
            ContextCompat.startForegroundService(context, locationServiceIntent)
        }
    }
}