package io.github.pknujsp.weatherwizard.feature.widget.activity.configure.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType

@Stable
data class WidgetModel(
    var id: Int = 0,
    var widgetType: WidgetType = WidgetType.ALL_IN_ONE,
    val save: () -> Unit,
    var addressName: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
) : UiModel {
    var weatherProvider: WeatherProvider by mutableStateOf(WeatherProvider.default)
    var locationType: LocationType by mutableStateOf(LocationType.default)
    var onSaved: Boolean by mutableStateOf(false)
}