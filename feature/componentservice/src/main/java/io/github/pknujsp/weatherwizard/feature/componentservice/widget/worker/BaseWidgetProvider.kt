package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.WidgetDeletedArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.WidgetUpdatedArgument
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@AndroidEntryPoint
abstract class BaseWidgetProvider : AppWidgetProvider() {

    @Inject lateinit var widgetDeleteBackgroundService: WidgetDeleteBackgroundService
    @Inject lateinit var widgetUpdateBackgroundService: WidgetUpdateBackgroundService
    @Inject @CoDispatcher(CoDispatcherType.IO) lateinit var ioDispatcher: CoroutineDispatcher

    protected companion object {
        val globalScope get() = GlobalScope
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        if (appWidgetIds.isNotEmpty()) {
            globalScope.launch(ioDispatcher) {
                widgetUpdateBackgroundService.run(WidgetUpdatedArgument(WidgetUpdatedArgument.UPDATE_ONLY_SPECIFIC_WIDGETS,
                    appWidgetIds.toTypedArray()))
            }
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        if (appWidgetIds.isNotEmpty()) {
            globalScope.launch(ioDispatcher) {
                widgetDeleteBackgroundService.run(WidgetDeletedArgument(appWidgetIds.toTypedArray()))
            }
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?) {
    }

}