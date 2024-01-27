package io.github.pknujsp.everyweather.feature.componentservice.notification.ongoing.model

import androidx.compose.runtime.Stable


@Stable
interface OngoingNotificationUiState {
    val ongoingNotificationSettings: OngoingNotificationSettings
    val isEnabled: Boolean
    val action: Action
    val changedCount: Int
    fun update()
    fun switch()

    enum class Action {
        ENABLED, DISABLED, UPDATED, NONE
    }
}