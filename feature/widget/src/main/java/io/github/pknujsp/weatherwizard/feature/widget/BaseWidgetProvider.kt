package io.github.pknujsp.weatherwizard.feature.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import io.github.pknujsp.weatherwizard.feature.widget.worker.WidgetWorker

open class BaseWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateWidget(context, appWidgetIds, WidgetManager.Action.UPDATE)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        updateWidget(context, appWidgetIds, WidgetManager.Action.DELETE)
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        intent.action?.run {
            if (this == WidgetManager.UPDATE_ALL_WIDGETS) {
                updateWidget(context, intArrayOf(), WidgetManager.Action.UPDATE)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    fun updateWidget(context: Context, appWidgetIds: IntArray, action: WidgetManager.Action) {
        println("BaseWidgetProvider.updateWidget: $action")
        val inputData = Data(mapOf("appWidgetIds" to appWidgetIds, "action" to action.name))
        val request = OneTimeWorkRequest.Builder(WidgetWorker::class.java).setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .addTag(WidgetWorker.name).setInputData(inputData).build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork(WidgetWorker.name, ExistingWorkPolicy.APPEND, request)
    }
}