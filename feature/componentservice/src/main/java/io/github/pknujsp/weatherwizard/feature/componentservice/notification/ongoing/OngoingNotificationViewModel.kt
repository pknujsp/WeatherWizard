package io.github.pknujsp.weatherwizard.feature.componentservice.notification.ongoing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.OngoingNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.model.OngoingNotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationSettingsEntity
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.ongoing.model.OngoingNotificationSettings
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.ongoing.model.OngoingNotificationUiState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OngoingNotificationViewModel @Inject constructor(
    private val ongoingNotificationRepository: OngoingNotificationRepository,
    val appAlarmManager: AppAlarmManager,
    appSettingsRepository: SettingsRepository,
) : ViewModel() {

    val units = appSettingsRepository.settings.replayCache.last().units

    private val _ongoingNotificationUiState = MutableOngoingNotificationUiState(
        ongoingNotificationSettings = OngoingNotificationSettingsEntity().let {
            OngoingNotificationSettings(
                notificationIconType = it.notificationIconType,
                refreshInterval = it.refreshInterval,
                weatherProvider = it.weatherProvider,
                location = it.location,
            )
        },
        update = ::update,
        switch = ::switch,
    )

    val ongoingNotificationUiState: OngoingNotificationUiState = _ongoingNotificationUiState

    init {
        viewModelScope.launch {
            val settingsEntity = ongoingNotificationRepository.getOngoingNotification()
            _ongoingNotificationUiState.apply {
                isInitialized = settingsEntity.isInitialized
                ongoingNotificationSettings = OngoingNotificationSettings(
                    id = settingsEntity.id,
                    notificationIconType = settingsEntity.data.notificationIconType,
                    refreshInterval = settingsEntity.data.refreshInterval,
                    weatherProvider = settingsEntity.data.weatherProvider,
                    location = settingsEntity.data.location,
                )
                isEnabled = settingsEntity.enabled
            }
        }
    }

    private fun switch() {
        viewModelScope.launch {
            ongoingNotificationRepository.switch(ongoingNotificationUiState.isEnabled)
            _ongoingNotificationUiState.action =
                if (ongoingNotificationUiState.isEnabled) OngoingNotificationUiState.Action.ENABLED else OngoingNotificationUiState.Action.DISABLED
            _ongoingNotificationUiState.changedCount++
        }
    }

    private fun update() {
        viewModelScope.launch {
            val settingsEntity = createSettingsEntity()
            ongoingNotificationRepository.updateOngoingNotification(settingsEntity)
            _ongoingNotificationUiState.action = OngoingNotificationUiState.Action.UPDATED
            _ongoingNotificationUiState.changedCount++
        }
    }

    private fun createSettingsEntity() = ongoingNotificationUiState.let {
        NotificationSettingsEntity(id = it.ongoingNotificationSettings.id,
            enabled = it.isEnabled,
            data = OngoingNotificationSettingsEntity(
                notificationIconType = it.ongoingNotificationSettings.notificationIconType,
                refreshInterval = it.ongoingNotificationSettings.refreshInterval,
                weatherProvider = it.ongoingNotificationSettings.weatherProvider,
                location = it.ongoingNotificationSettings.location,
            ),
            isInitialized = true)
    }
}


private class MutableOngoingNotificationUiState(
    ongoingNotificationSettings: OngoingNotificationSettings,
    var isInitialized: Boolean = false,
    private val update: () -> Unit,
    private val switch: () -> Unit,
) : OngoingNotificationUiState {

    override var isEnabled by mutableStateOf(false)
    override var action by mutableStateOf(OngoingNotificationUiState.Action.NONE)
    override var ongoingNotificationSettings by mutableStateOf(ongoingNotificationSettings)
    override var changedCount by mutableIntStateOf(0)

    override fun switch() {
        isEnabled = !isEnabled
        if (isInitialized) {
            switch.invoke()
        }
    }

    override fun update() {
        update.invoke()
    }

}