package io.github.pknujsp.weatherwizard.core.model.notification

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider

abstract class SavedNotificationValuesEntity {
    var weatherProvider: WeatherDataProvider = WeatherDataProvider.default
    var locationType: LocationType = LocationType.default
}

@Stable
abstract class SavedNotificationValuesUiModel(savedNotificationValuesEntity: SavedNotificationValuesEntity) : UiModel {
    var weatherProvider: WeatherDataProvider by mutableStateOf(savedNotificationValuesEntity.weatherProvider)
    var locationType: LocationType by mutableStateOf(savedNotificationValuesEntity.locationType)
}