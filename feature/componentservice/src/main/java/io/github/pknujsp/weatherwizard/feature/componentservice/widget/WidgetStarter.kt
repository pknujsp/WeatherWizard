package io.github.pknujsp.weatherwizard.feature.componentservice.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.pknujsp.weatherwizard.core.common.manager.WidgetManager

class WidgetStarterImpl(
    private val widgetManager: WidgetManager
) : WidgetStarter {

    private fun startWidget(context: Context) {
        val installedWidgetIds = widgetManager.installedAllWidgetIds
        if (installedWidgetIds.isEmpty()) {
            return
        }

        widgetManager.getProviderByWidgetId(installedWidgetIds.first())?.let { widgetProvider ->
            // 위젯 업데이트
            val intent = Intent().apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                component = widgetProvider
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, installedWidgetIds.toIntArray())
            }
            context.sendBroadcast(intent)
            Log.d("WidgetStarterImpl", "sendBroadcast: $installedWidgetIds, $widgetProvider")

            // 위젯 자동 업데이트 예약
        }
    }

    override suspend fun start(context: Context) {
        startWidget(context)
    }

}


interface WidgetStarter {
    suspend fun start(context: Context)
}