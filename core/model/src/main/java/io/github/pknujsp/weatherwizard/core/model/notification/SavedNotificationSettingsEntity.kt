package io.github.pknujsp.weatherwizard.core.model.notification

import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider

abstract class SavedNotificationSettingsEntity {
    var weatherProvider: WeatherDataProvider = WeatherDataProvider.default
    var locationType: LocationType = LocationType.default
}