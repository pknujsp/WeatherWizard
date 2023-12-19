package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.WidgetServiceArgument
import io.github.pknujsp.weatherwizard.feature.componentservice.ComponentPendingIntentManager
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.DailyNotificationService
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.ongoing.OngoingNotificationService
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.WidgetWorker


abstract class BaseWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        if (appWidgetIds.isNotEmpty()) {
            val workRequest = OneTimeWorkRequestBuilder<WidgetDeleteWorker>().addTag(WidgetDeleteWorker.name).setInputData(Data.Builder()
                .putAll(WidgetServiceArgument(ComponentServiceAction.Widget.WidgetAction.DELETE.name, appWidgetIds.toTypedArray()).toMap())
                .build()).build()
            val workManager = WorkManager.getInstance(context)
            workManager.enqueue(workRequest)
        }
    }

    override fun onEnabled(context: Context) {

    }

    override fun onDisabled(context: Context) {

    }

}