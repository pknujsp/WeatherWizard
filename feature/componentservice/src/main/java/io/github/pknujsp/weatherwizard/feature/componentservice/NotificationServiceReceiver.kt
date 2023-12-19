package io.github.pknujsp.weatherwizard.feature.componentservice

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.worker.DailyNotificationService
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.ongoing.worker.OngoingNotificationService


class NotificationServiceReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        intent.action?.let {
            if (intent.extras == null) {
                return
            }
            Log.d("NotificationService", "NotificationServiceReceiver onReceive ${intent.extras}, ${intent.action}")

            val workRequest = when (val action = ComponentServiceAction.toInstance(intent.extras!!)) {
                is ComponentServiceAction.OngoingNotification -> {
                    OneTimeWorkRequestBuilder<OngoingNotificationService>().setInputData(Data.Builder().putAll(action.argument.toMap())
                        .build()).addTag(OngoingNotificationService.name).build()
                }

                is ComponentServiceAction.DailyNotification -> {
                    OneTimeWorkRequestBuilder<DailyNotificationService>().setInputData(Data.Builder().putAll(action.argument.toMap())
                        .build()).addTag(OngoingNotificationService.name).build()
                }

                is ComponentServiceAction.Widget -> {
                    OneTimeWorkRequestBuilder<DailyNotificationService>().setInputData(Data.Builder().putAll(action.argument.toMap())
                        .build()).addTag(OngoingNotificationService.name).build()
                }
            }
            val workManager = WorkManager.getInstance(context)
            workManager.enqueue(workRequest)
        }
    }

    @Suppress("DEPRECATION")
    private fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
        return (getSystemService(ACTIVITY_SERVICE) as ActivityManager).getRunningServices(Integer.MAX_VALUE)
            .any { it -> it.service.className == service.name }
    }


}