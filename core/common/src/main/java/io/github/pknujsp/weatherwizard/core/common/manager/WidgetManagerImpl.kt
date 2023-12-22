package io.github.pknujsp.weatherwizard.core.common.manager

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.common.R
import io.github.pknujsp.weatherwizard.core.common.enum.pendingIntentRequestFactory
import kotlin.reflect.KClass

class WidgetManagerImpl(context: Context) : WidgetManager {
    private val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)

    override val installedAllWidgetIds: List<Int>
        get() = appWidgetManager.installedProviders.flatMap { providerInfo ->
            appWidgetManager.getAppWidgetIds(providerInfo.provider).toList()
        }

    override fun updateWidget(appWidgetId: Int, remoteView: RemoteViews, context: Context, activityCls: KClass<*>) {
        remoteView.setOnClickPendingIntent(android.R.id.background,
            getDialogPendingIntent(context, activityCls))
        appWidgetManager.updateAppWidget(appWidgetId, remoteView)
    }

    override fun isBind(appWidgetId: Int): Boolean {
        val info = appWidgetManager.getAppWidgetInfo(appWidgetId)
        return info != null
    }

    private fun getDialogPendingIntent(context: Context, activityCls: KClass<*>) = PendingIntent.getActivity(context,
        pendingIntentRequestFactory.requestId(activityCls.hashCode()),
        Intent(context, activityCls.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
}

interface WidgetManager {
    val installedAllWidgetIds: List<Int>
    fun updateWidget(appWidgetId: Int, remoteView: RemoteViews, context: Context, activityCls: KClass<*>)
    fun isBind(appWidgetId: Int): Boolean
}