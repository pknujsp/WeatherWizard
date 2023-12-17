package io.github.pknujsp.weatherwizard.feature.notification.ongoing.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class OngoingNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != null) {
            val locationServiceIntent = Intent(context, OngoingNotificationService::class.java).apply {
                action = OngoingNotificationService.ACTION_START_LOCATION_SERVICE
            }
            context.startService(locationServiceIntent)
            Log.d("OngoingNotificationReceiver", "onReceive: ${intent.action}")
        }
    }
}