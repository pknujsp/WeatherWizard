package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.WidgetServiceArgument
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

@OptIn(DelicateCoroutinesApi::class)
@AndroidEntryPoint
abstract class BaseWidgetProvider : AppWidgetProvider() {

    protected companion object {
        val globalScope get() = GlobalScope
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d("BaseWidgetProvider", "onUpdate: ${appWidgetIds.contentToString()}")
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        if (appWidgetIds.isNotEmpty()) {
            val workRequest = OneTimeWorkRequestBuilder<WidgetDeleteWorker>().addTag(WidgetDeleteWorker.name).setInputData(Data.Builder()
                .putAll(WidgetServiceArgument(ComponentServiceAction.Widget.WidgetAction.DELETE.name, appWidgetIds.toTypedArray()).toMap())
                .build()).build()
            val workManager = WorkManager.getInstance(context)
            workManager.enqueue(workRequest)
        }
        Log.d("BaseWidgetProvider", "onDeleted: ${appWidgetIds.contentToString()}")
    }

    override fun onEnabled(context: Context) {
        Log.d("BaseWidgetProvider", "onEnabled")
    }

    override fun onDisabled(context: Context) {
        Log.d("BaseWidgetProvider", "onDisabled")
    }

    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?) {
        Log.d("BaseWidgetProvider", "onAppWidgetOptionsChanged: $appWidgetId")
    }

}