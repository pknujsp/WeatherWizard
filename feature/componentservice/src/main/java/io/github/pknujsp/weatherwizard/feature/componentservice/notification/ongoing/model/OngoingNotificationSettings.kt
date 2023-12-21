package io.github.pknujsp.weatherwizard.feature.componentservice.notification.ongoing.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

class OngoingNotificationSettings(
    val id: Long = 0L,
    notificationIconType: NotificationIconType,
    refreshInterval: RefreshInterval,
    weatherProvider: WeatherProvider,
    location: LocationTypeModel,
) : UiModel {
    var notificationIconType by mutableStateOf(notificationIconType)
    var refreshInterval by mutableStateOf(refreshInterval)
    var weatherProvider by mutableStateOf(weatherProvider)
    var location by mutableStateOf(location)
}