package io.github.pknujsp.weatherwizard.feature.notification.daily.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.feature.notification.daily.model.DailyNotificationSettings
import io.github.pknujsp.weatherwizard.feature.notification.daily.model.list.DailyNotificationListUiState
import io.github.pknujsp.weatherwizard.feature.notification.daily.model.list.DailyNotificationSettingsListItemUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.nio.file.Files.delete
import javax.inject.Inject

@HiltViewModel
class DailyNotificationListViewModel @Inject constructor(
    private val dailyNotificationRepository: DailyNotificationRepository,
) : ViewModel() {

    var notifications by mutableStateOf(DailyNotificationListUiState(switch = ::switch))
        private set

    init {
        viewModelScope.launch {
            dailyNotificationRepository.getDailyNotifications().collectLatest {
                val list = it.map { entity ->
                    DailyNotificationSettingsListItemUiState(id = entity.id,
                        type = entity.data.type,
                        locationType = entity.data.locationType,
                        hour = entity.data.hour,
                        minute = entity.data.minute,
                        weatherDataProvider = entity.data.weatherProvider,
                        isEnabled = entity.enabled)
                }
                notifications = notifications.copy(notifications = list)
            }
        }
    }

    private fun switch(id: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            dailyNotificationRepository.switch(id, isEnabled)
        }
    }
}