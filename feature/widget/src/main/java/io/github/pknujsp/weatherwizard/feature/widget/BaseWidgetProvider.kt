package io.github.pknujsp.weatherwizard.feature.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import io.github.pknujsp.weatherwizard.feature.widget.worker.OngoingNotificationWorker
import io.github.pknujsp.weatherwizard.feature.widget.worker.WidgetWorker

abstract class BaseWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateWidget(context, appWidgetIds, WidgetManager.Action.UPDATE)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        updateWidget(context, appWidgetIds, WidgetManager.Action.DELETE)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @SuppressLint("RestrictedApi")
    fun updateWidget(context: Context, appWidgetIds: IntArray, action: WidgetManager.Action) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val inputData = Data(mapOf("appWidgetIds" to appWidgetIds, "action" to action.name))

        val request = OneTimeWorkRequest.Builder(WidgetWorker::class.java).setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .addTag(WidgetWorker.name).setInputData(inputData).build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork(WidgetWorker.name, ExistingWorkPolicy.APPEND_OR_REPLACE, request)
    }
}