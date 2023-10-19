package io.github.pknujsp.weatherwizard.core.model.notification

import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider

data class OngoingNotificationInfo(
    override var latitude: Double = 0.0,
    override var longitude: Double = 0.0,
    override var addressName: String = "",
    override var locationType: LocationType = LocationType.CurrentLocation,
    var refreshInterval: RefreshInterval = RefreshInterval.MANUAL,
    override var weatherProvider: WeatherDataProvider = WeatherDataProvider.default,
    var notificationIconType: NotificationIconType = NotificationIconType.TEMPERATURE,
) : NotificationUiModel