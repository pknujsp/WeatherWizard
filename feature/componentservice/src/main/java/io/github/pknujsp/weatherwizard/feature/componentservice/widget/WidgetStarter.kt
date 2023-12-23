package io.github.pknujsp.weatherwizard.feature.componentservice.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker.SummaryWeatherWidgetProvider

class WidgetStarterImpl(
    private val widgetManager: WidgetManager
) : WidgetStarter {

    private fun startWidget(context: Context) {
        val installedWidgetIds = widgetManager.installedAllWidgetIds
        if (installedWidgetIds.isEmpty()) {
            return
        }

        widgetManager.getProviderByWidgetId(installedWidgetIds.first())?.let { widgetProvider ->
            val intent = Intent().apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                component = widgetProvider
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, installedWidgetIds.toIntArray())
            }
            context.sendBroadcast(intent)
            Log.d("WidgetStarterImpl", "sendBroadcast: $installedWidgetIds, $widgetProvider")
        }
    }

    override suspend fun start(context: Context) {
        startWidget(context)
    }

}


interface WidgetStarter {
    suspend fun start(context: Context)
}