package io.github.pknujsp.weatherwizard.core.model.notification

import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

abstract class SavedNotificationSettingsEntity {
    var weatherProvider: WeatherProvider = WeatherProvider.default
    var locationType: LocationType = LocationType.default
}