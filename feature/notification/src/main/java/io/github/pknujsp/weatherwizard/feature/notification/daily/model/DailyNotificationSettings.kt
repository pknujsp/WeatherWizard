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

class DailyNotificationSettings(
    val id: Long = 0L,
    type: DailyNotificationType,
    locationType: LocationType,
    hour: Int,
    minute: Int,
) : UiModel {
    var type by mutableStateOf(type)
    var locationType by mutableStateOf(locationType)
    var hour by mutableIntStateOf(hour)
    var minute by mutableIntStateOf(minute)
}