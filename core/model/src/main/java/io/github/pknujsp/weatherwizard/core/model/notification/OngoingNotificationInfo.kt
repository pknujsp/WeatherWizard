package io.github.pknujsp.weatherwizard.core.model.notification

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider

data class OngoingNotificationInfo(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var addressName: String = "",
    var locationType: LocationType = LocationType.CurrentLocation,
    var refreshInterval: RefreshInterval = RefreshInterval.MANUAL,
    var weatherProvider: WeatherDataProvider = WeatherDataProvider.default,
    var notificationIconType: NotificationIconType = NotificationIconType.TEMPERATURE,
) : UiModel