package io.github.pknujsp.weatherwizard.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class WidgetStarterImpl(
    private val widgetManager: WidgetManager
) : WidgetStarter {

    private fun startWidget(context: Context) {
        val installedWidgetIds = widgetManager.installedAllWidgetIds
        if (installedWidgetIds.isEmpty()) {
            return
        }

        widgetManager.getProviderByWidgetId(installedWidgetIds.first())?.let { widgetProvider ->
            Intent(context, widgetProvider.javaClass).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, installedWidgetIds.toTypedArray())
                context.sendBroadcast(this)
            }
        }
    }

    override suspend fun start(context: Context) {
        supervisorScope {
            launch {
                //startWidget(context)
            }
        }
    }

}


interface WidgetStarter {
    suspend fun start(context: Context)
}