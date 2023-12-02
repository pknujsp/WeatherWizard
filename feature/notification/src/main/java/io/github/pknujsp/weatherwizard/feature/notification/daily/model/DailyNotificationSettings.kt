package io.github.pknujsp.weatherwizard.feature.notification.daily.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider

class DailyNotificationSettings(
    val id: Long = 0L,
    type: DailyNotificationType,
    locationType: LocationType,
    hour: Int,
    minute: Int,
    weatherDataProvider: WeatherDataProvider
) : UiModel {
    var type by mutableStateOf(type)
    var locationType by mutableStateOf(locationType)
    var hour by mutableIntStateOf(hour)
    var minute by mutableIntStateOf(minute)
    var weatherDataProvider by mutableStateOf(weatherDataProvider)

    private val timeFormat = "%02d:%02d"
    val timeText: String
        get() = timeFormat.format(hour, minute)
}