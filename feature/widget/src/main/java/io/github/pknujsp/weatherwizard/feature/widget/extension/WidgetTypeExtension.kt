package io.github.pknujsp.weatherwizard.feature.widget.extension

import android.content.ComponentName
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.feature.widget.summary.SummaryWeatherWidgetProvider

fun WidgetType.Companion.fromProvider(componentName: ComponentName) = when (componentName.className) {
    SummaryWeatherWidgetProvider::class.java.name -> WidgetType.ALL_IN_ONE
    else -> WidgetType.ALL_IN_ONE
}