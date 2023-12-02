package io.github.pknujsp.weatherwizard.feature.notification.ongoing.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider

class OngoingNotificationSettings(
    val id: Long = 0L,
    notificationIconType: NotificationIconType,
    refreshInterval: RefreshInterval,
    weatherProvider: WeatherDataProvider,
    locationType: LocationType,
) : UiModel {
    var notificationIconType by mutableStateOf(notificationIconType)
    var refreshInterval by mutableStateOf(refreshInterval)
    var weatherProvider by mutableStateOf(weatherProvider)
    var locationType by mutableStateOf(locationType)
}