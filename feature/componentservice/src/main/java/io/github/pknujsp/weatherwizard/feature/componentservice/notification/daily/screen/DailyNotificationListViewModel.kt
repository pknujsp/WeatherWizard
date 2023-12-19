package io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.model.list.DailyNotificationListUiState
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily.model.list.DailyNotificationSettingsListItem
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.manager.NotificationAlarmManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyNotificationListViewModel @Inject constructor(
    private val dailyNotificationRepository: DailyNotificationRepository, alarmManager: AppAlarmManager
) : ViewModel() {
    val notificationAlarmManager = NotificationAlarmManager(alarmManager)
    var notifications by mutableStateOf(DailyNotificationListUiState())
        private set

    init {
        viewModelScope.launch {
            dailyNotificationRepository.getDailyNotifications().collectLatest {
                val list = it.map { entity ->
                    DailyNotificationSettingsListItem(id = entity.id,
                        type = entity.data.type,
                        location = entity.data.location,
                        hour = entity.data.hour,
                        minute = entity.data.minute,
                        weatherProvider = entity.data.weatherProvider,
                        isEnabled = entity.enabled)
                }
                notifications = notifications.copy(notifications = list)
            }
        }
    }

    fun switch(id: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            dailyNotificationRepository.switch(id, isEnabled)
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            dailyNotificationRepository.deleteDailyNotification(id)
        }
    }
}