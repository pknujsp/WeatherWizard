package io.github.pknujsp.weatherwizard.core.model.notification.ongoing

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.SavedNotificationValuesEntity
import io.github.pknujsp.weatherwizard.core.model.notification.SavedNotificationValuesUiModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider

class OngoingSavedNotificationValuesEntity : SavedNotificationValuesEntity() {
    var notificationIconType: NotificationIconType = NotificationIconType.default
    var refreshInterval: RefreshInterval = RefreshInterval.default
}

@Stable
class OngoingNotificationValuesUiModel(
    ongoingSavedNotificationValuesEntity: OngoingSavedNotificationValuesEntity
) : SavedNotificationValuesUiModel(ongoingSavedNotificationValuesEntity) {
    var notificationIconType: NotificationIconType by mutableStateOf(ongoingSavedNotificationValuesEntity.notificationIconType)
    var refreshInterval: RefreshInterval by mutableStateOf(ongoingSavedNotificationValuesEntity.refreshInterval)
}