package io.github.pknujsp.weatherwizard.core.model.notification

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider

interface NotificationUiModel : UiModel {
    var weatherProvider: WeatherDataProvider
    var locationType: LocationType
    var addressName: String
    var latitude: Double
    var longitude: Double
}