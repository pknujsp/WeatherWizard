package io.github.pknujsp.weatherwizard.core.common.manager

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.common.enum.pendingIntentRequestFactory
import kotlin.reflect.KClass

internal class WidgetManagerImpl(context: Context) : WidgetManager {
    private val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)

    override fun updateWidget(appWidgetId: Int, remoteView: RemoteViews, context: Context, activityCls: KClass<*>) {
        remoteView.setOnClickPendingIntent(remoteView.layoutId, getDialogPendingIntent(context, activityCls))
        appWidgetManager.updateAppWidget(appWidgetId, remoteView)
    }

    override fun isBind(appWidgetId: Int) = appWidgetManager.getAppWidgetInfo(appWidgetId) != null

    private fun getDialogPendingIntent(context: Context, activityCls: KClass<*>) =
        PendingIntent.getActivity(context, pendingIntentRequestFactory.requestId(activityCls), Intent(context, activityCls.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    override fun getUpdatePendingIntent(
        context: Context, action: WidgetManager.Action, appWidgetIds: IntArray, cls: KClass<*>
    ): PendingIntent = PendingIntent.getBroadcast(context,
        pendingIntentRequestFactory.requestId(WidgetManager.Action.UPDATE_ALL_WIDGETS::class),
        Intent(context, cls.java).apply {
            this.action = action.name
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


    override fun getInitPendingIntent(context: Context, appWidgetIds: IntArray, cls: KClass<*>): PendingIntent = PendingIntent.getBroadcast(
        context,
        pendingIntentRequestFactory.requestId(WidgetManager.Action.INIT_NEW_WIDGET::class),
        Intent(context, cls.java).apply {
            action = WidgetManager.Action.INIT_NEW_WIDGET.name
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        },
        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

}

interface WidgetManager {
    fun updateWidget(appWidgetId: Int, remoteView: RemoteViews, context: Context, activityCls: KClass<*>)
    fun isBind(appWidgetId: Int): Boolean
    fun getUpdatePendingIntent(
        context: Context, action: Action, appWidgetIds: IntArray = intArrayOf(), cls: KClass<*>
    ): PendingIntent

    fun getInitPendingIntent(context: Context, appWidgetIds: IntArray, cls: KClass<*>): PendingIntent

    enum class Action {
        INIT_NEW_WIDGET, DELETE, UPDATE_ONLY_BASED_CURRENT_LOCATION, UPDATE_ALL_WIDGETS, UPDATE_ONLY_WITH_WIDGETS
    }
}