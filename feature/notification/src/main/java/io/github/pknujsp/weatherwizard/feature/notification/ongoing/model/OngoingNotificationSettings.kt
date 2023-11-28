package io.github.pknujsp.weatherwizard.feature.notification.ongoing.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.SavedNotificationValuesUiModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.model.OngoingNotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider

@Stable
class OngoingNotificationSettings(
    ongoingSavedNotificationValuesEntity: OngoingNotificationSettingsEntity
) : SavedNotificationValuesUiModel(ongoingSavedNotificationValuesEntity) {
    var notificationIconType: NotificationIconType by mutableStateOf(ongoingSavedNotificationValuesEntity.notificationIconType)
    var refreshInterval: RefreshInterval by mutableStateOf(ongoingSavedNotificationValuesEntity.refreshInterval)
    var weatherProvider: WeatherDataProvider by mutableStateOf(savedNotificationSettingsEntity.weatherProvider)
    var locationType: LocationType by mutableStateOf(savedNotificationSettingsEntity.locationType)
}