package io.github.pknujsp.weatherwizard.feature.notification

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.NotificationAction
import io.github.pknujsp.weatherwizard.feature.notification.daily.DailyNotificationService
import io.github.pknujsp.weatherwizard.feature.notification.manager.NotificationService
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.OngoingNotificationService


class NotificationServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        intent.action?.let {
            if (intent.extras == null) {
                return
            }
            Log.d("NotificationService", "NotificationServiceReceiver onReceive ${intent.extras}, ${intent.action}")

            val workManager = WorkManager.getInstance(context)

            val workRequest = when (val action = NotificationAction.toInstance(intent.extras!!)) {
                is NotificationAction.Ongoing -> {
                    OneTimeWorkRequestBuilder<OngoingNotificationService>().addTag(OngoingNotificationService.name).build()
                }

                is NotificationAction.Daily -> {
                    OneTimeWorkRequestBuilder<DailyNotificationService>().setInputData(Data.Builder().putAll(action.toMap()).build())
                        .addTag(OngoingNotificationService.name).build()
                }
            }

            workManager.enqueue(workRequest)
        }
    }

    @Suppress("DEPRECATION")
    private fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
        return (getSystemService(ACTIVITY_SERVICE) as ActivityManager).getRunningServices(Integer.MAX_VALUE)
            .any { it -> it.service.className == service.name }
    }
}