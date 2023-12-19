package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context


abstract class BaseWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {

    }

    override fun onEnabled(context: Context) {

    }

    override fun onDisabled(context: Context) {

    }

}