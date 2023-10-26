package io.github.pknujsp.weatherwizard.feature.widget.extension

import android.content.ComponentName
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.feature.widget.summary.SummaryWeatherWidgetProvider

fun WidgetType.Companion.fromProvider(componentName: ComponentName) = when (componentName.className) {
    SummaryWeatherWidgetProvider::class.java.name -> WidgetType.SUMMARY
    else -> WidgetType.SUMMARY
}

private val widgetProviders = listOf(
    ComponentName(SummaryWeatherWidgetProvider::class.java.`package`!!.name, SummaryWeatherWidgetProvider::class.java.name),
)

fun WidgetType.Companion.widgetProviders() = widgetProviders