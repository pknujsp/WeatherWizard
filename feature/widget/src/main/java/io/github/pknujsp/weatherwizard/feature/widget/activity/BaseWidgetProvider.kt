package io.github.pknujsp.weatherwizard.feature.widget.activity

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager

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
        val bundle = bundleOf("action" to action.name, "appWidgetIds" to (appWidgetIds ?: intArrayOf()))
    }

    private fun getWorkerClass(action: WidgetManager.Action) = when (action) {
        WidgetManager.Action.UPDATE_ONLY_WITH_WIDGETS, WidgetManager.Action.INIT_NEW_WIDGET, WidgetManager.Action.UPDATE_ONLY_BASED_CURRENT_LOCATION, WidgetManager.Action.UPDATE_ALL_WIDGETS -> WidgetWorker::class.java
        WidgetManager.Action.DELETE -> WidgetDeleteWorker::class.java
    }
}