package io.github.pknujsp.everyweather.feature.componentservice.widget.widgetprovider

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
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.widgetnotification.model.WidgetUpdatedArgument
import io.github.pknujsp.everyweather.feature.componentservice.manager.AppComponentServiceManagerFactory
import io.github.pknujsp.everyweather.feature.componentservice.manager.WidgetAlarmManager
import io.github.pknujsp.everyweather.feature.componentservice.widget.worker.FakeWorker
import io.github.pknujsp.everyweather.feature.componentservice.widget.worker.WidgetUpdateBackgroundService
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

    @Inject
    @CoDispatcher(CoDispatcherType.IO)
    lateinit var dispatcher: CoroutineDispatcher

    companion object {
        private val globalScope get() = GlobalScope
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        if (appWidgetIds.isNotEmpty()) {
            Log.d("WidgetProvider", "onUpdate: ${appWidgetIds.contentToString()}")
            launchWork(WidgetUpdatedArgument(WidgetUpdatedArgument.DRAW, appWidgetIds.toTypedArray()))
        }
    }

    override fun onDeleted(
        context: Context,
        appWidgetIds: IntArray,
    ) {
        if (appWidgetIds.isNotEmpty()) {
            Log.d("WidgetProvider", "onDeleted: ${appWidgetIds.contentToString()}")
            launchWork(WidgetUpdatedArgument(WidgetUpdatedArgument.DELETE, appWidgetIds.toTypedArray()))
        }
    }

    override fun onEnabled(context: Context) {
        Log.d("WidgetProvider", "onEnabled")
        WorkManager.getInstance(context).enqueueFakeWork()
    }

    override fun onDisabled(context: Context) {
        Log.d("WidgetProvider", "onDisabled")
        val pendingResult = goAsync()
        globalScope.launch(dispatcher) {
            // WorkManager.getInstance(context).cancelFakeWork()
            AppComponentServiceManagerFactory.getManager(context, WidgetAlarmManager::class).unScheduleAutoRefresh()
            launchWork(WidgetUpdatedArgument(WidgetUpdatedArgument.DELETE_ALL)).join()
        }.invokeOnCompletion {
            pendingResult.finish()
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?,
    ) {
        Log.d("WidgetProvider", "onAppWidgetOptionsChanged: $appWidgetId")
        launchWork(WidgetUpdatedArgument(WidgetUpdatedArgument.DRAW, arrayOf(appWidgetId)))
    }

    override fun onReceive(
        context: Context?,
        intent: Intent?,
    ) {
        super.onReceive(context, intent)
        intent?.action.let {
            if (it == Intent.ACTION_BOOT_COMPLETED || it == Intent.ACTION_MY_PACKAGE_REPLACED && context != null) {
                WorkManager.getInstance(context!!).enqueueFakeWork()
                launchWork(WidgetUpdatedArgument(WidgetUpdatedArgument.DRAW_ALL))
            }
        }
    }

    private fun launchWork(argument: WidgetUpdatedArgument) =
        globalScope.launch(dispatcher) {
            widgetUpdateBackgroundService.run(argument)
        }
}

private const val FAKE_WORK_NAME = "always_pending_work"

fun WorkManager.enqueueFakeWork() {
    val work = getWorkInfosForUniqueWork(FAKE_WORK_NAME)
    if (work.isDone or work.isCancelled or work.get().isEmpty()) {
        val alwaysPendingWork = OneTimeWorkRequestBuilder<FakeWorker>().setInitialDelay(5000L, TimeUnit.DAYS).build()
        enqueueUniqueWork(FAKE_WORK_NAME, ExistingWorkPolicy.KEEP, alwaysPendingWork)
    }
}

fun WorkManager.cancelFakeWork() {
    cancelUniqueWork(FAKE_WORK_NAME)
}
