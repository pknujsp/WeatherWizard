package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.content.ComponentName
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType

fun WidgetType.Companion.fromProvider(componentName: ComponentName) = when (componentName.className) {
    SummaryWeatherWidgetProvider::class.java.name -> WidgetType.ALL_IN_ONE
    else -> WidgetType.ALL_IN_ONE
}