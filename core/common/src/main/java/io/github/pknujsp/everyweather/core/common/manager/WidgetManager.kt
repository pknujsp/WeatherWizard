package io.github.pknujsp.everyweather.core.common.manager

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlin.reflect.KClass

private class WidgetManagerImpl(private val context: Context) : WidgetManager {
    override val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)
    private val packageName: String = context.packageName

    override val installedAllWidgetIds: List<Int>
        get() =
            appWidgetManager.installedProviders.filter {
                it.provider.packageName == packageName
            }.flatMap { providerInfo ->
                appWidgetManager.getAppWidgetIds(providerInfo.provider).toList()
            }

    override fun getProviderByWidgetId(appWidgetId: Int) = appWidgetManager.getAppWidgetInfo(appWidgetId)?.provider

    override fun updateWidget(
        appWidgetId: Int,
        remoteView: RemoteViews,
        context: Context,
        activityCls: KClass<*>,
    ) {
        remoteView.setOnClickPendingIntent(android.R.id.background, getDialogPendingIntent(context, activityCls))
        appWidgetManager.updateAppWidget(appWidgetId, remoteView)
    }

    override fun isBind(appWidgetId: Int): Boolean {
        val info = appWidgetManager.getAppWidgetInfo(appWidgetId)
        return info != null
    }

    override fun redrawAllWidgets(): Boolean {
        val installedWidgetIds = installedAllWidgetIds
        if (installedWidgetIds.isEmpty()) {
            return false
        }

        Intent().apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            component = getProviderByWidgetId(installedWidgetIds.first())
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, installedWidgetIds.toIntArray())
            context.sendBroadcast(this)
        }

        return true
    }

    private fun getDialogPendingIntent(
        context: Context,
        activityCls: KClass<*>,
    ) = PendingIntent.getActivity(
        context,
        activityCls.hashCode(),
        Intent(context, activityCls.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}

interface WidgetManager : AppComponentManager {
    companion object : AppComponentManagerInitializer {
        private var instance: WidgetManager? = null

        override fun getInstance(context: Context): WidgetManager =
            synchronized(this) {
                instance ?: WidgetManagerImpl(context).also { instance = it }
            }
    }

    val appWidgetManager: AppWidgetManager
    val installedAllWidgetIds: List<Int>

    fun updateWidget(
        appWidgetId: Int,
        remoteView: RemoteViews,
        context: Context,
        activityCls: KClass<*>,
    )

    fun isBind(appWidgetId: Int): Boolean

    fun getProviderByWidgetId(appWidgetId: Int): ComponentName?

    fun redrawAllWidgets(): Boolean
}
