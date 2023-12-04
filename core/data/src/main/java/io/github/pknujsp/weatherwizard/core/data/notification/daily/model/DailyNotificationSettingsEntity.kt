package io.github.pknujsp.weatherwizard.core.data.notification.daily.model

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

data class DailyNotificationSettingsEntity(
    val type: DailyNotificationType = DailyNotificationType.default,
    val weatherProvider: WeatherProvider = WeatherProvider.default,
    val locationType: LocationType = LocationType.default,
    val hour: Int = 12,
    val minute: Int = 30,
) : EntityModel