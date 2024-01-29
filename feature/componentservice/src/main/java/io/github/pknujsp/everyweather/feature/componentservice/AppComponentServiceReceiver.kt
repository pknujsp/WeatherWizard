package io.github.pknujsp.everyweather.feature.componentservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.github.pknujsp.everyweather.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.everyweather.feature.componentservice.notification.daily.DailyNotificationCoroutineService
import io.github.pknujsp.everyweather.feature.componentservice.notification.ongoing.OngoingNotificationCoroutineService
import io.github.pknujsp.everyweather.feature.componentservice.widget.WidgetCoroutineService


class AppComponentServiceReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_PROCESS = "APP_COMPONENT_SERVICE_ACTION"
        const val ACTION_REFRESH = "APP_COMPONENT_SERVICE_ACTION_REFRESH"
        const val ACTION_AUTO_REFRESH = "APP_COMPONENT_SERVICE_ACTION_AUTO_REFRESH"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.extras == null) {
            return
        }

        intent.action?.let {
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

}