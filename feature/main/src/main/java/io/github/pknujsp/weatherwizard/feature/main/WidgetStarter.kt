package io.github.pknujsp.weatherwizard.feature.main

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager

class WidgetStarterImpl(
    private val widgetManager: WidgetManager
) : WidgetStarter {

    private fun startWidget(context: Context) {
        val installedWidgetIds = widgetManager.installedAllWidgetIds
        if (installedWidgetIds.isEmpty()) {
            return
        }

        widgetManager.getProviderByWidgetId(installedWidgetIds.first())?.let { componentName ->
            val intent = Intent(context, componentName.javaClass).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, installedWidgetIds.toTypedArray())
            }
            context.sendBroadcast(intent)
        }
    }

    override suspend fun start(context: Context) {
        startWidget(context)
    }

}


interface WidgetStarter {
    suspend fun start(context: Context)
}