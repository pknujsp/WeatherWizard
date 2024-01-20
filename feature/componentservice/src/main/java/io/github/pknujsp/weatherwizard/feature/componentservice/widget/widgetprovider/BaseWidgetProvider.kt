package io.github.pknujsp.weatherwizard.feature.componentservice.widget.widgetprovider

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.WidgetUpdatedArgument
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.widgetInProgress
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker.WidgetUpdateBackgroundService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@AndroidEntryPoint
abstract class BaseWidgetProvider : AppWidgetProvider() {

    @Inject lateinit var widgetUpdateBackgroundService: WidgetUpdateBackgroundService
    @Inject @CoDispatcher(CoDispatcherType.MULTIPLE) lateinit var dispatcher: CoroutineDispatcher

    companion object {
        private val globalScope get() = GlobalScope
        const val ACTION_SCHEDULE_TO_AUTO_REFRESH = "wyther.intent.action.SCHEDULE_TO_AUTO_REFRESH"
        private val specialActions = setOf(ACTION_SCHEDULE_TO_AUTO_REFRESH, Intent.ACTION_BOOT_COMPLETED)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        if (appWidgetIds.isNotEmpty()) {
            Log.d("WidgetProvider", "onUpdate: ${appWidgetIds.contentToString()}")
            globalScope.launch(dispatcher) {
                widgetUpdateBackgroundService.run(WidgetUpdatedArgument(WidgetUpdatedArgument.UPDATE_ONLY_SPECIFIC_WIDGETS,
                    appWidgetIds.toTypedArray()))
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        if (appWidgetIds.isNotEmpty()) {
            Log.d("WidgetProvider", "onDeleted: ${appWidgetIds.contentToString()}")
            globalScope.launch(dispatcher) {
                widgetUpdateBackgroundService.run(WidgetUpdatedArgument(WidgetUpdatedArgument.DELETE, appWidgetIds.toTypedArray()))
            }
        }
    }

    override fun onEnabled(context: Context) {

    }

    override fun onDisabled(context: Context) {
    }

    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?) {
        globalScope.launch(dispatcher) {
            Log.d("WidgetProvider", "onAppWidgetOptionsChanged: $appWidgetId")
            widgetUpdateBackgroundService.run(WidgetUpdatedArgument(WidgetUpdatedArgument.UPDATE_ONLY_SPECIFIC_WIDGETS,
                arrayOf(appWidgetId)))
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        intent?.action?.also { action ->
            if (action in specialActions) {
                globalScope.launch(dispatcher) {
                    if (action == Intent.ACTION_BOOT_COMPLETED) {
                        widgetUpdateBackgroundService.run(WidgetUpdatedArgument(WidgetUpdatedArgument.UPDATE_ALL))
                    } else if (action == ACTION_SCHEDULE_TO_AUTO_REFRESH) {
                        widgetUpdateBackgroundService.run(WidgetUpdatedArgument(WidgetUpdatedArgument.SCHEDULE_TO_AUTO_REFRESH))
                    }
                }
            }
        }

        Log.d("WidgetProvider", "onReceive: ${intent?.action}")
    }
}