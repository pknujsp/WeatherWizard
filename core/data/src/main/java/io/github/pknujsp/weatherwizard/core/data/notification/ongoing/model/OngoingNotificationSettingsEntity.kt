package io.github.pknujsp.weatherwizard.core.data.notification.ongoing.model

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

data class OngoingNotificationSettingsEntity(
    val notificationIconType: NotificationIconType = NotificationIconType.default,
    val refreshInterval: RefreshInterval = RefreshInterval.default,
    val weatherProvider: WeatherProvider = WeatherProvider.default,
    val locationType: LocationType = LocationType.default,
) : EntityModel