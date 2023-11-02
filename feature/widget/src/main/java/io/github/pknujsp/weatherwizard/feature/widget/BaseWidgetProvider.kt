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
import io.github.pknujsp.weatherwizard.feature.widget.worker.WidgetDeleteWorker
import io.github.pknujsp.weatherwizard.feature.widget.worker.WidgetWorker

abstract class BaseWidgetProvider : AppWidgetProvider() {

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
        intent.action?.let { action ->
            if (action == WidgetManager.Action.UPDATE_ALL_WIDGETS.name) {
                updateWidget(context, intArrayOf(), WidgetManager.Action.valueOf(action))
            } else if (action == WidgetManager.Action.UPDATE_WIDGETS_BASE_CURRENT_LOCATION.name) {
                updateWidget(context, intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)!!, WidgetManager.Action.valueOf(action))
            }
        }
    }

    @SuppressLint("RestrictedApi")
    fun updateWidget(context: Context, appWidgetIds: IntArray, action: WidgetManager.Action) {
        println("BaseWidgetProvider.updateWidget: $action - ${appWidgetIds.contentToString()}")
        val inputData = Data(mapOf("appWidgetIds" to appWidgetIds, "action" to action.name))
        val workerClass = getWorkerClass(action)

        val request =
            OneTimeWorkRequest.Builder(workerClass).setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST).setInputData(inputData)
                .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    private fun getWorkerClass(action: WidgetManager.Action) = when (action) {
        WidgetManager.Action.UPDATE, WidgetManager.Action.UPDATE_ONLY_BASED_CURRENT_LOCATION, WidgetManager.Action.UPDATE_ALL_WIDGETS, WidgetManager.Action.UPDATE_WIDGETS_BASE_CURRENT_LOCATION -> WidgetWorker::class.java
        WidgetManager.Action.DELETE -> WidgetDeleteWorker::class.java
    }
}