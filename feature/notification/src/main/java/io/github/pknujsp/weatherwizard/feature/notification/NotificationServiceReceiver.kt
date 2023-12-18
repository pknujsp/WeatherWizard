package io.github.pknujsp.weatherwizard.feature.notification

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import io.github.pknujsp.weatherwizard.feature.notification.manager.FakeNotificationService
import io.github.pknujsp.weatherwizard.feature.notification.manager.NotificationService


class NotificationServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        intent.action?.let {
            Log.d("NotificationService", "NotificationServiceReceiver onReceive ${intent.extras}, ${intent.action}")
            val service = Intent(context, NotificationService::class.java).apply {
                action = it
                if (intent.extras != null) {
                    putExtras(intent.extras!!)
                }
            }
            ContextCompat.startForegroundService(context, service)
        }
    }

    @Suppress("DEPRECATION")
    private fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
        return (getSystemService(ACTIVITY_SERVICE) as ActivityManager).getRunningServices(Integer.MAX_VALUE)
            .any { it -> it.service.className == service.name }
    }
}