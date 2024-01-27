package io.github.pknujsp.everyweather.core.data.notification.daily.model

import io.github.pknujsp.everyweather.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.everyweather.core.model.EntityModel
import io.github.pknujsp.everyweather.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider

data class DailyNotificationSettingsEntity(
    val type: DailyNotificationType = DailyNotificationType.default,
    val weatherProvider: WeatherProvider = WeatherProvider.default,
    val location: LocationTypeModel = LocationTypeModel(),
    val hour: Int = 12,
    val minute: Int = 30,
) : EntityModel