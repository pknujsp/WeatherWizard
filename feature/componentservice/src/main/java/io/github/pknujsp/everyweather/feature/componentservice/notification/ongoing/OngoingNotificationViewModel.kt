package io.github.pknujsp.everyweather.feature.componentservice.notification.ongoing

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.data.notification.ongoing.OngoingNotificationRepository
import io.github.pknujsp.everyweather.core.data.notification.ongoing.model.OngoingNotificationSettingsEntity
import io.github.pknujsp.everyweather.core.data.settings.SettingsRepository
import io.github.pknujsp.everyweather.core.model.notification.NotificationSettingsEntity
import io.github.pknujsp.everyweather.feature.componentservice.notification.ongoing.model.OngoingNotificationSettings
import io.github.pknujsp.everyweather.feature.componentservice.notification.ongoing.model.OngoingNotificationUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OngoingNotificationViewModel
@Inject constructor(
    private val ongoingNotificationRepository: OngoingNotificationRepository,
    private val appSettingsRepository: SettingsRepository,
    @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    val units get() = appSettingsRepository.settings.replayCache.last().units

    private val mutableNotificationUiState = MutableOngoingNotificationUiState(
        ongoingNotificationSettings = OngoingNotificationSettingsEntity().let {
            OngoingNotificationSettings(
                notificationIconType = it.notificationIconType,
                refreshInterval = it.refreshInterval,
                weatherProvider = it.weatherProvider,
                location = it.location,
            )
        },
        update = ::update,
    )

    val notificationUiState: OngoingNotificationUiState = mutableNotificationUiState

    init {
        viewModelScope.launch {
            val settingsEntity = withContext(ioDispatcher) {
                ongoingNotificationRepository.getOngoingNotification()
            }

            mutableNotificationUiState.apply {
                settings = OngoingNotificationSettings(
                    id = settingsEntity.id,
                    notificationIconType = settingsEntity.data.notificationIconType,
                    refreshInterval = settingsEntity.data.refreshInterval,
                    weatherProvider = settingsEntity.data.weatherProvider,
                    location = settingsEntity.data.location,
                )
                isEnabled = settingsEntity.enabled
                action = OngoingNotificationUiState.Action.LOADED
            }
        }
    }

    private fun update() {
        viewModelScope.launch {
            withContext(ioDispatcher) {
                ongoingNotificationRepository.run {
                    switch(notificationUiState.isEnabled)
                    updateOngoingNotification(createSettingsEntity())
                }
            }
            mutableNotificationUiState.run {
                action = OngoingNotificationUiState.Action.UPDATED
                onChanged()
            }
        }
    }

    private fun createSettingsEntity() = notificationUiState.let {
        NotificationSettingsEntity(
            id = it.settings.id,
            enabled = it.isEnabled,
            data = OngoingNotificationSettingsEntity(
                notificationIconType = it.settings.notificationIconType,
                refreshInterval = it.settings.refreshInterval,
                weatherProvider = it.settings.weatherProvider,
                location = it.settings.location,
            ),
            isInitialized = true,
        )
    }
}

@Stable
private class MutableOngoingNotificationUiState(
    ongoingNotificationSettings: OngoingNotificationSettings,
    private val update: () -> Unit,
) : OngoingNotificationUiState {
    override var isEnabled: Boolean by mutableStateOf(false)
    override var action by mutableStateOf(OngoingNotificationUiState.Action.LOADING)
    override var settings by mutableStateOf(ongoingNotificationSettings)
    override var isChanged by mutableIntStateOf(0)
        private set

    override fun update(action: OngoingNotificationUiState.Action) {
        this.action = action
        onChanged()
        if (action == OngoingNotificationUiState.Action.UPDATE) {
            update()
        }
    }

    override fun switch(enabled: Boolean) {
        isEnabled = enabled
    }

    fun onChanged() {
        isChanged++
    }
}