package io.github.pknujsp.weatherwizard.feature.notification.ongoing.model

import androidx.compose.runtime.Stable


@Stable
interface OngoingNotificationUiState {
    val ongoingNotificationSettings: OngoingNotificationSettings
    val isEnabled: Boolean
    val action: Action
    fun update()
    fun switch()

    enum class Action {
        ENABLED, DISABLED, UPDATED, NONE
    }
}