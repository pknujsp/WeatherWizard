package io.github.pknujsp.weatherwizard.feature.widget.activity.configure.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.feature.widget.activity.configure.ConfigureActionState
import kotlinx.coroutines.flow.StateFlow

@Stable
data class WidgetModel(
    var id: Int = 0,
    var widgetType: WidgetType = WidgetType.SUMMARY,
    val save: () -> Unit,
    var addressName: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
) : UiModel {
    var weatherProvider: WeatherDataProvider by mutableStateOf(WeatherDataProvider.default)
    var locationType: LocationType by mutableStateOf(LocationType.default)
    var onSaved: Boolean by mutableStateOf(false)
}