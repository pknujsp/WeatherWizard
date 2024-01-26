package io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.model

import androidx.compose.runtime.Stable


@Stable
interface DailyNotificationUiState {
    val isNew: Boolean
    val isEnabled: Boolean
    val action: Action
    val dailyNotificationSettings: DailyNotificationSettings

    fun update()

    sealed interface Action {
        data class UPDATED(val id: Long) : Action
        data class ENABLED(val id: Long) : Action
        data class DISABLED(val id: Long) : Action
        data object NONE : Action
    }
}