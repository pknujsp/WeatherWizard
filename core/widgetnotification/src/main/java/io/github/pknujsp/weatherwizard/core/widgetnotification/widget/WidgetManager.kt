package io.github.pknujsp.weatherwizard.core.widgetnotification.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.common.enum.pendingIntentRequestFactory
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.remoteview.WidgetRemoteViewsCreator
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.summary.SummaryRemoteViewCreator
import kotlin.reflect.KClass

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

    inline fun <reified C : WidgetRemoteViewsCreator<out UiModel>> remoteViewCreator(
        widgetType: WidgetType
    ): C = when (widgetType) {
        WidgetType.ALL_IN_ONE -> SummaryRemoteViewCreator() as C
        else -> throw IllegalArgumentException("Unknown widget type: $widgetType")
    }

    fun updateWidget(appWidgetId: Int, remoteView: RemoteViews, context: Context, activityCls: KClass<*>) {
        remoteView.setOnClickPendingIntent(remoteView.layoutId, getDialogPendingIntent(context, activityCls))
        appWidgetManager.updateAppWidget(appWidgetId, remoteView)
    }

    fun isBind(appWidgetId: Int) = appWidgetManager.getAppWidgetInfo(appWidgetId) != null

    private fun getDialogPendingIntent(context: Context, activityCls: KClass<*>) =
        PendingIntent.getActivity(context, pendingIntentRequestFactory.requestId(activityCls), Intent(context, activityCls.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    fun getUpdatePendingIntent(context: Context, action: Action, appWidgetIds: IntArray = intArrayOf(), cls: KClass<*>): PendingIntent =
        PendingIntent.getBroadcast(context,
            pendingIntentRequestFactory.requestId(Action.UPDATE_ALL_WIDGETS::class),
            Intent(context, cls.java).apply {
                this.action = action.name
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


    fun getInitPendingIntent(context: Context, appWidgetIds: IntArray, cls: KClass<*>): PendingIntent = PendingIntent.getBroadcast(context,
        pendingIntentRequestFactory.requestId(Action.INIT_NEW_WIDGET::class),
        Intent(context, cls.java).apply {
            action = Action.INIT_NEW_WIDGET.name
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        },
        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

    enum class Action {
        INIT_NEW_WIDGET, DELETE, UPDATE_ONLY_BASED_CURRENT_LOCATION, UPDATE_ALL_WIDGETS, UPDATE_ONLY_WITH_WIDGETS
    }
}