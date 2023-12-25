package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.content.ComponentName
import android.content.Context
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType

internal fun WidgetType.Companion.fromProvider(componentName: ComponentName) = when (componentName.className) {
    SummaryWeatherWidgetProvider::class.java.name -> WidgetType.ALL_IN_ONE
    else -> WidgetType.ALL_IN_ONE
}

internal fun WidgetType.Companion.toComponentName(context: Context, type: WidgetType): ComponentName {
    val widgetProviderClass = when (type) {
        WidgetType.ALL_IN_ONE -> SummaryWeatherWidgetProvider::class
        else -> SummaryWeatherWidgetProvider::class
    }
    return ComponentName(context, widgetProviderClass.java)
}