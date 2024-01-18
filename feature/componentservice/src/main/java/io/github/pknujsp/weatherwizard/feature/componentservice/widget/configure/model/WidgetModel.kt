package io.github.pknujsp.weatherwizard.feature.componentservice.widget.configure.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType

@Stable
class WidgetModel(
    val widgetId: Int,
    widgetType: Int,
    val save: () -> Unit,
) : UiModel {
    val widgetType = WidgetType.fromKey(widgetType)
    var weatherProvider: WeatherProvider by mutableStateOf(WeatherProvider.default)
    var location: LocationTypeModel by mutableStateOf(LocationTypeModel())
    val displayAllWeatherProviders by mutableStateOf(this.widgetType == WidgetType.DAILY_FORECAST_COMPARISON || this.widgetType ==
            WidgetType.HOURLY_FORECAST_COMPARISON)
}