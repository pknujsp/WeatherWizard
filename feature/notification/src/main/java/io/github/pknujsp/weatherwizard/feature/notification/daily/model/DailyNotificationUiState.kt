package io.github.pknujsp.weatherwizard.feature.notification.daily.model

import androidx.compose.runtime.Stable
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.model.OngoingNotificationUiState


@Stable
interface DailyNotificationUiState {
    val isNew: Boolean
    val isEnabled: Boolean
    val action: Action
    val dailyNotificationSettings: DailyNotificationSettings

    fun update()
    fun switch()

    enum class Action {
        ENABLED, DISABLED, UPDATED, NONE
    }
}