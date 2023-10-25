package io.github.pknujsp.weatherwizard.feature.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.feature.widget.activity.WidgetActivity
import io.github.pknujsp.weatherwizard.feature.widget.extension.widgetProviders
import io.github.pknujsp.weatherwizard.feature.widget.remoteview.WidgetRemoteViewsCreator
import io.github.pknujsp.weatherwizard.feature.widget.summary.SummaryRemoteViewCreator
import io.github.pknujsp.weatherwizard.feature.widget.summary.SummaryWeatherWidgetProvider

class WidgetManager private constructor(context: Context) {
    private val appWidgetManager = AppWidgetManager.getInstance(context)

    val widgetIds = WidgetType.widgetProviders().map {
        appWidgetManager.getAppWidgetIds(it).toList()
    }.flatten()

    companion object {
        const val UPDATE_ALL_WIDGETS = "widget_type"
        private var instance: WidgetManager? = null

        fun getInstance(context: Context): WidgetManager {
            if (instance == null) {
                instance = WidgetManager(context)
            }
            return instance!!
        }
    }

    inline fun <reified C : WidgetRemoteViewsCreator<out UiModel>> remoteViewCreator(
        widgetType: WidgetType
    ): C = when (widgetType) {
        WidgetType.SUMMARY -> SummaryRemoteViewCreator() as C
        else -> throw IllegalArgumentException("Unknown widget type: $widgetType")
    }

    fun updateWidget(appWidgetId: Int, remoteView: RemoteViews, context: Context) {
        remoteView.setOnClickPendingIntent(remoteView.layoutId, getDialogPendingIntent(context))
        appWidgetManager.updateAppWidget(appWidgetId, remoteView)
    }

    private fun getDialogPendingIntent(context: Context) =
        PendingIntent.getActivity(context, System.currentTimeMillis().toInt(), Intent(context, WidgetActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    fun getUpdatePendingIntent(context: Context, appWidgetIds: IntArray? = null): PendingIntent {
        return PendingIntent.getBroadcast(context, 10000 + if (appWidgetIds == null) 10 else 0, Intent(context,
            SummaryWeatherWidgetProvider::class.java)
            .apply {
                if (appWidgetIds != null) {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                }
            }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    enum class Action {
        UPDATE, DELETE
    }
}