package io.github.pknujsp.weatherwizard.feature.widget

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import io.github.pknujsp.weatherwizard.feature.widget.worker.WidgetDeleteWorker
import io.github.pknujsp.weatherwizard.feature.widget.worker.WidgetWorker

abstract class BaseWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        enqueueWork(context, WidgetManager.Action.DELETE, appWidgetIds)
    }

    override fun onEnabled(context: Context) {

    }

    override fun onDisabled(context: Context) {

    }


    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        println("BaseWidgetProvider.onReceive: ${intent.action}")

        if (intent.action != null) {
            val appWidgetIds = intent.extras?.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
            WidgetManager.Action.entries.find { it.name == intent.action }?.let {
                enqueueWork(context, it, appWidgetIds)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    fun enqueueWork(context: Context, action: WidgetManager.Action, appWidgetIds: IntArray?) {
        println("BaseWidgetProvider.updateWidget: $action")

        val inputData = Data(mapOf("appWidgetIds" to (appWidgetIds ?: intArrayOf()), "action" to action.name))
        val workerClass = getWorkerClass(action)

        val request =
            OneTimeWorkRequest.Builder(workerClass).setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST).setInputData(inputData)
                .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    private fun getWorkerClass(action: WidgetManager.Action) = when (action) {
        WidgetManager.Action.UPDATE_ONLY_WITH_WIDGETS, WidgetManager.Action.INIT_NEW_WIDGET, WidgetManager.Action.UPDATE_ONLY_BASED_CURRENT_LOCATION, WidgetManager.Action.UPDATE_ALL_WIDGETS -> WidgetWorker::class.java

        WidgetManager.Action.DELETE -> WidgetDeleteWorker::class.java
    }
}