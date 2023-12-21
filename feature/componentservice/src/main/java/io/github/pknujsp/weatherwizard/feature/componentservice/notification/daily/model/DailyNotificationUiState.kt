package io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.model

import androidx.compose.runtime.Stable


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