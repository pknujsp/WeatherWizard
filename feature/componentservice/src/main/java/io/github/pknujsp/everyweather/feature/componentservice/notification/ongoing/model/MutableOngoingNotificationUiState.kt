package io.github.pknujsp.everyweather.feature.componentservice.notification.ongoing.model

import androidx.compose.runtime.Stable

@Stable
interface OngoingNotificationUiState {
    val settings: OngoingNotificationSettings
    val isEnabled: Boolean
    val action: Action
    val isChanged: Int
    fun update()
    fun switch()
    enum class Action {
        ENABLED,
        DISABLED,
        UPDATED,
        NONE;

        companion object{
            val isOn = setOf(ENABLED, UPDATED)
        }
    }
}