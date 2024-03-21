package io.github.pknujsp.everyweather.core.data.notification.ongoing.model

import io.github.pknujsp.everyweather.core.model.EntityModel
import io.github.pknujsp.everyweather.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.everyweather.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.everyweather.core.model.notification.enums.OngoingNotificationType
import io.github.pknujsp.everyweather.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider

data class OngoingNotificationSettingsEntity(
    val notificationIconType: NotificationIconType = NotificationIconType.default,
    val refreshInterval: RefreshInterval = RefreshInterval.default,
    val weatherProvider: WeatherProvider = WeatherProvider.default,
    val location: LocationTypeModel = LocationTypeModel(),
    val type: OngoingNotificationType = OngoingNotificationType.default,
) : EntityModel
