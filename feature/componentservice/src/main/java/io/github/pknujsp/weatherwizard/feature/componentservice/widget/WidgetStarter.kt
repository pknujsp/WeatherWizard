package io.github.pknujsp.weatherwizard.feature.componentservice.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.pknujsp.weatherwizard.core.common.enum.pendingIntentRequestFactory
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.feature.componentservice.ComponentPendingIntentManager

class WidgetStarterImpl(
    private val widgetManager: WidgetManager
) : WidgetStarter {

    private fun startWidget(context: Context) {
        val installedWidgetIds = widgetManager.installedAllWidgetIds
        if (installedWidgetIds.isEmpty()) {
            return
        }

        widgetManager.getProviderByWidgetId(installedWidgetIds.first())?.let { widgetProvider ->
            val intent = Intent(context, widgetProvider.javaClass).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, installedWidgetIds.toTypedArray())
            }
            PendingIntent.getBroadcast(context,
                pendingIntentRequestFactory.requestId(intent.hashCode()),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE).send()
            Log.d("WidgetStarterImpl", "sendBroadcast: $installedWidgetIds")
        }
    }

    override suspend fun start(context: Context) {
        startWidget(context)
    }

}


interface WidgetStarter {
    suspend fun start(context: Context)
}