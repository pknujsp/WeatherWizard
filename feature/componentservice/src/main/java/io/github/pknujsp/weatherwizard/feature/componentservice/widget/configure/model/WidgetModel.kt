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
data class WidgetModel(
    var id: Int = 0,
    var widgetType: WidgetType = WidgetType.ALL_IN_ONE,
    val save: () -> Unit,
) : UiModel {
    var weatherProvider: WeatherProvider by mutableStateOf(WeatherProvider.default)
    var location: LocationTypeModel by mutableStateOf(LocationTypeModel())
}