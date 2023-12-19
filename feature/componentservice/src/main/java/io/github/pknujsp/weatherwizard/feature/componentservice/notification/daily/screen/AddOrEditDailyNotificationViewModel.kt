package io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.notification.daily.model.DailyNotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationSettingsEntity
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.NotificationRoutes
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.model.DailyNotificationSettings
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.model.DailyNotificationUiState
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.manager.NotificationAlarmManager
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddOrEditDailyNotificationViewModel @Inject constructor(
    private val dailyNotificationRepository: DailyNotificationRepository,
    appAlarmManager: AppAlarmManager,
    savedStateHandle: SavedStateHandle,
    appSettingsRepository: SettingsRepository
) : ViewModel() {
    val notificationAlarmManager = NotificationAlarmManager(appAlarmManager)
    private val notificationId = savedStateHandle.get<Long>(NotificationRoutes.AddOrEditDaily.arguments.first().name) ?: -1L
    private val isNew get() = notificationId == -1L
    val units = appSettingsRepository.currentUnits.value

    private val _dailyNoficationUiState =
        MutableDailyNotificationUiState(dailyNotificationSettings = DailyNotificationSettingsEntity().let {
            DailyNotificationSettings(type = it.type,
                location = it.location,
                hour = it.hour,
                minute = it.minute,
                weatherProvider = it.weatherProvider)
        }, update = ::update, switch = ::switch, isNew = isNew)

    val dailyNotificationUiState: DailyNotificationUiState = _dailyNoficationUiState

    init {
        viewModelScope.launch {
            if (!isNew) {
                dailyNotificationRepository.getDailyNotification(notificationId).run {
                    _dailyNoficationUiState.dailyNotificationSettings = DailyNotificationSettings(id = id,
                        type = data.type,
                        location = data.location,
                        hour = data.hour,
                        minute = data.minute,
                        weatherProvider = data.weatherProvider)
                    _dailyNoficationUiState.isEnabled = enabled
                }
            }
        }
    }

    private fun createSettingsEntity() = dailyNotificationUiState.dailyNotificationSettings.let {
        DailyNotificationSettingsEntity(
            type = it.type,
            location = it.location,
            hour = it.hour,
            minute = it.minute,
        )
    }

    private fun update() {
        viewModelScope.launch {
            _dailyNoficationUiState.action = DailyNotificationUiState.Action.NONE
            _dailyNoficationUiState.isEnabled= dailyNotificationUiState.isEnabled || isNew
            val settingsEntity = NotificationSettingsEntity(
                id = dailyNotificationUiState.dailyNotificationSettings.id,
                enabled = dailyNotificationUiState.isEnabled,
                data = createSettingsEntity(),
                isInitialized = true,
            )
            dailyNotificationRepository.updateDailyNotification(settingsEntity)
            _dailyNoficationUiState.action = DailyNotificationUiState.Action.UPDATED

            Log.d("AddOrEditDailyNotificationViewModel", "update: ${dailyNotificationUiState.dailyNotificationSettings}")
        }
    }

    private fun switch() {
        viewModelScope.launch {
            _dailyNoficationUiState.action = DailyNotificationUiState.Action.NONE
            dailyNotificationRepository.switch(dailyNotificationUiState.dailyNotificationSettings.id, dailyNotificationUiState.isEnabled)
            _dailyNoficationUiState.action =
                if (dailyNotificationUiState.isEnabled) DailyNotificationUiState.Action.ENABLED else DailyNotificationUiState.Action.DISABLED
        }
    }
}


private class MutableDailyNotificationUiState(
    dailyNotificationSettings: DailyNotificationSettings,
    private val update: () -> Unit,
    private val switch: () -> Unit,
    override val isNew: Boolean
) : DailyNotificationUiState {
    override var dailyNotificationSettings by mutableStateOf(dailyNotificationSettings)
    override var isEnabled by mutableStateOf(false)
    override var action by mutableStateOf(DailyNotificationUiState.Action.NONE)

    override fun update() {
        update.invoke()
    }

    override fun switch() {
        isEnabled = !isEnabled
        switch.invoke()
    }
}