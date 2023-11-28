package io.github.pknujsp.weatherwizard.core.data.notification.ongoing.model

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import kotlinx.serialization.SerialName

data class OngoingNotificationSettingsEntity(
    val notificationIconType: NotificationIconType = NotificationIconType.default,
    val refreshInterval: RefreshInterval = RefreshInterval.default,
    val weatherProvider: WeatherDataProvider = WeatherDataProvider.default,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val addressName: String = "",
    val locationType: LocationType = LocationType.default,
) : EntityModel