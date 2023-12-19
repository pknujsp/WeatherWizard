package io.github.pknujsp.weatherwizard.core.common.manager

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.common.enum.pendingIntentRequestFactory
import kotlin.reflect.KClass

class WidgetManagerImpl(context: Context) : WidgetManager {
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
}

interface WidgetManager {
    fun updateWidget(appWidgetId: Int, remoteView: RemoteViews, context: Context, activityCls: KClass<*>)
    fun isBind(appWidgetId: Int): Boolean
}