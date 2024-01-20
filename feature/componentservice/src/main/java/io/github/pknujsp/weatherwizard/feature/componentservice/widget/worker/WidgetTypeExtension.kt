package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import android.content.ComponentName
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.widgetprovider.DailyForecastComparisonWidgetProvider
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.widgetprovider.HourlyForecastComparisonWidgetProvider
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.widgetprovider.SummaryWeatherWidgetProvider
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.widgetprovider.TimeHourlyForecastWidgetProvider

internal fun WidgetType.Companion.fromComponentName(componentName: ComponentName) = when (componentName.className) {
    SummaryWeatherWidgetProvider::class.java.name -> WidgetType.ALL_IN_ONE
    TimeHourlyForecastWidgetProvider::class.java.name -> WidgetType.TIME_HOURLY_FORECAST
    HourlyForecastComparisonWidgetProvider::class.java.name -> WidgetType.HOURLY_FORECAST_COMPARISON
    DailyForecastComparisonWidgetProvider::class.java.name -> WidgetType.DAILY_FORECAST_COMPARISON
    else -> {
        error("Unknown widget provider: ${componentName.className}")
    }
}