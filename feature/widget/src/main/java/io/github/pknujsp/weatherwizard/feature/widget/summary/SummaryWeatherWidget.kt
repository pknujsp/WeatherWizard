package io.github.pknujsp.weatherwizard.feature.widget.summary

import android.appwidget.AppWidgetManager
import android.content.Context
import io.github.pknujsp.weatherwizard.feature.widget.BaseWidgetProvider


class SummaryWeatherWidget : BaseWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {

    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}