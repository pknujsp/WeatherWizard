package io.github.pknujsp.everyweather.feature.componentservice.notification.ongoing.model

import androidx.compose.runtime.Stable

@Stable
interface OngoingNotificationUiState {
    val settings: OngoingNotificationSettings
    val isEnabled: Boolean
    val action: Action
    val isChanged: Int
    fun update(action: Action)
    fun switch(enabled: Boolean)
    enum class Action {
        CHECK_UPDATE, UPDATE, LOADING, LOADED, UPDATED;
    }
}