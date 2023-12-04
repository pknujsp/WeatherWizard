package io.github.pknujsp.weatherwizard.feature.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.feature.widget.activity.WidgetActivity
import io.github.pknujsp.weatherwizard.feature.widget.remoteview.WidgetRemoteViewsCreator
import io.github.pknujsp.weatherwizard.feature.widget.summary.SummaryRemoteViewCreator
import io.github.pknujsp.weatherwizard.feature.widget.summary.SummaryWeatherWidgetProvider

class WidgetManager private constructor(context: Context) {
    private val appWidgetManager = AppWidgetManager.getInstance(context)
    val widgetIds get() = appWidgetManager.installedProviders.flatMap { appWidgetManager.getAppWidgetIds(it.provider).toList() }

    companion object {
        private var instance: WidgetManager? = null

        fun getInstance(context: Context): WidgetManager {
            if (instance == null) {
                instance = WidgetManager(context)
            }
            return instance!!
        }
    }

    /**
     * Returns a WidgetRemoteViewsCreator based on the provided WidgetType.
     * The returned creator is cast to the generic type C.
     * @throws IllegalArgumentException if an unknown WidgetType is provided.
     */
    inline fun <reified C : WidgetRemoteViewsCreator<out UiModel>> remoteViewCreator(
        widgetType: WidgetType
    ): C = when (widgetType) {
        WidgetType.ALL_IN_ONE -> SummaryRemoteViewCreator() as C
        else -> throw IllegalArgumentException("Unknown widget type: $widgetType")
    }

    /**
     * Updates a widget with the provided appWidgetId.
     * The widget is updated with the provided RemoteViews.
     */
    fun updateWidget(appWidgetId: Int, remoteView: RemoteViews, context: Context) {
        println("WidgetManager.updateWidget: $appWidgetId")
        remoteView.setOnClickPendingIntent(remoteView.layoutId, getDialogPendingIntent(context))
        appWidgetManager.updateAppWidget(appWidgetId, remoteView)
    }

    /**
     * Checks if a widget with the provided appWidgetId is bound.
     * @return true if the widget is bound, false otherwise.
     */
    fun isBind(appWidgetId: Int) = appWidgetManager.getAppWidgetInfo(appWidgetId) != null

    /**
     * Returns a PendingIntent for starting a WidgetActivity.
     */
    private fun getDialogPendingIntent(context: Context) =
        PendingIntent.getActivity(context, 10000, Intent(context, WidgetActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    /**
     * Returns a PendingIntent for broadcasting an update to widgets.
     * The PendingIntent includes the provided action and appWidgetIds.
     */
    fun getUpdatePendingIntent(context: Context, action: Action, appWidgetIds: IntArray = intArrayOf()): PendingIntent {
        return PendingIntent.getBroadcast(context, 11000, Intent(context, SummaryWeatherWidgetProvider::class.java).apply {
            this.action = action.name
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    /**
     * Returns a PendingIntent for initializing widgets.
     * The PendingIntent includes the provided appWidgetIds.
     */
    fun getInitPendingIntent(context: Context, appWidgetIds: IntArray): PendingIntent {
        return PendingIntent.getBroadcast(context, 12000, Intent(context, SummaryWeatherWidgetProvider::class.java).apply {
            action = Action.INIT_NEW_WIDGET.name
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        }, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
    }

    /**
     * Enum class for defining actions that can be performed on widgets.
     */
    enum class Action {
        INIT_NEW_WIDGET, DELETE, UPDATE_ONLY_BASED_CURRENT_LOCATION, UPDATE_ALL_WIDGETS, UPDATE_ONLY_WITH_WIDGETS
    }
}