package io.github.pknujsp.weatherwizard.feature.componentservice.widget.widgetprovider

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.WidgetUpdatedArgument
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker.FakeWorker
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker.WidgetUpdateBackgroundService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@OptIn(DelicateCoroutinesApi::class)
@AndroidEntryPoint
abstract class BaseWidgetProvider : AppWidgetProvider() {

    @Inject lateinit var widgetUpdateBackgroundService: WidgetUpdateBackgroundService
    @Inject @CoDispatcher(CoDispatcherType.IO) lateinit var dispatcher: CoroutineDispatcher

    companion object {
        private const val FAKE_WORK_NAME = "always_pending_work"
        private val globalScope get() = GlobalScope
        private val specialActions = setOf(Intent.ACTION_BOOT_COMPLETED)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        if (appWidgetIds.isNotEmpty()) {
            Log.d("WidgetProvider", "onUpdate: ${appWidgetIds.contentToString()}")
            launchWork(WidgetUpdatedArgument(WidgetUpdatedArgument.UPDATE_ONLY_SPECIFIC_WIDGETS, appWidgetIds.toTypedArray()))
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        if (appWidgetIds.isNotEmpty()) {
            Log.d("WidgetProvider", "onDeleted: ${appWidgetIds.contentToString()}")
            launchWork(WidgetUpdatedArgument(WidgetUpdatedArgument.DELETE, appWidgetIds.toTypedArray()))
        }
    }

    override fun onEnabled(context: Context) {
        val alwaysPendingWork = OneTimeWorkRequestBuilder<FakeWorker>().setInitialDelay(5000L, TimeUnit.DAYS).build()
        WorkManager.getInstance(context).enqueueUniqueWork(FAKE_WORK_NAME, ExistingWorkPolicy.KEEP, alwaysPendingWork)
    }

    override fun onDisabled(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(FAKE_WORK_NAME)
    }

    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?) {
        Log.d("WidgetProvider", "onAppWidgetOptionsChanged: $appWidgetId")
        launchWork(WidgetUpdatedArgument(WidgetUpdatedArgument.UPDATE_ONLY_SPECIFIC_WIDGETS, arrayOf(appWidgetId)))
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        intent?.action?.also { action ->
            if (action in specialActions) {
                if (action == Intent.ACTION_BOOT_COMPLETED) {
                    launchWork(WidgetUpdatedArgument(WidgetUpdatedArgument.UPDATE_ALL))
                }
            }
        }
    }

    private fun launchWork(argument: WidgetUpdatedArgument) {
        globalScope.launch(dispatcher) {
            widgetUpdateBackgroundService.run(argument)
        }
    }
}