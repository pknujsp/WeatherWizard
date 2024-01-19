package io.github.pknujsp.weatherwizard.feature.componentservice

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.DailyNotificationCoroutineService
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.ongoing.OngoingNotificationCoroutineService
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.WidgetCoroutineService


class NotificationServiceReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_PROCESS = "NOTIFICATION_SERVICE_ACTION"
    }

    override fun onReceive(context: Context, intent: Intent) {
        intent.action?.let {
            if (intent.extras == null) {
                return
            }
            val workRequest = when (val action = ComponentServiceAction.toInstance(intent.extras!!)) {
                is ComponentServiceAction.OngoingNotification -> {
                    OneTimeWorkRequestBuilder<OngoingNotificationCoroutineService>().setInputData(Data.Builder()
                        .putAll(action.argument.toMap()).build()).addTag(OngoingNotificationCoroutineService.name).build()
                }

                is ComponentServiceAction.DailyNotification -> {
                    OneTimeWorkRequestBuilder<DailyNotificationCoroutineService>().setInputData(Data.Builder()
                        .putAll(action.argument.toMap()).build()).addTag(DailyNotificationCoroutineService.name).build()
                }

                is ComponentServiceAction.LoadWidgetData -> {
                    val builder = OneTimeWorkRequestBuilder<WidgetCoroutineService>().addTag(WidgetCoroutineService.name)
                    builder.setInputData(Data.Builder().putAll(action.argument.toMap()).build()).build()
                }

                else -> {
                    return
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