package io.github.pknujsp.weatherwizard.feature.notification.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.feature.notification.daily.model.DailyNotificationSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyNotificationListViewModel @Inject constructor(
    private val dailyNotificationRepository: DailyNotificationRepository
) : ViewModel() {

    private val _onChangedStateNotification = MutableSharedFlow<DailyNotificationSettings>()
    val onChangedStateNotification: SharedFlow<DailyNotificationSettings> = _onChangedStateNotification

    val notifications = dailyNotificationRepository.getDailyNotifications().map { entities ->
        entities.map { entity ->
            DailyNotificationSettings(
                id = entity.id,
                enabled = entity.enabled,
                locationType = entity.data.getLocationType(),
                type = entity.data.getType(),
                hour = entity.data.hour,
                minute = entity.data.minute,
                address = entity.data.addressName,
                switch = ::switch,
                delete = ::delete,
            )
        }
    }.flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private fun switch(notification: DailyNotificationSettings) {
        viewModelScope.launch {
            dailyNotificationRepository.switch(notification.id, notification.isEnabled)
            _onChangedStateNotification.emit(notification)
        }
    }

    private fun delete(notification: DailyNotificationSettings) {
        viewModelScope.launch {
            dailyNotificationRepository.deleteDailyNotification(notification.id)
            _onChangedStateNotification.emit(notification)
        }
    }
}