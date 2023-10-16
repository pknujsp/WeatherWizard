package io.github.pknujsp.weatherwizard.feature.notification.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.notification.NotificationRepository
import io.github.pknujsp.weatherwizard.core.model.notification.daily.DailyNotificationSimpleInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyNotificationListViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    val notifications = notificationRepository.getDailyNotifications().map { entities ->
        entities.map { entity ->
            DailyNotificationSimpleInfo(
                id = entity.idInDb,
                enabled = entity.enabled,
                locationType = entity.data.getLocationType(),
                type = entity.data.getType(),
                hour = entity.data.hour,
                minute = entity.data.minute,
                switch = ::switch,
                delete = ::delete,
            )
        }
    }.flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private fun switch(notification: DailyNotificationSimpleInfo) {
        viewModelScope.launch {
            notificationRepository.switch(notification.id, notification.enabled)
        }
    }

    private fun delete(notification: DailyNotificationSimpleInfo) {
        viewModelScope.launch {
            notificationRepository.deleteDailyNotification(notification.id)
        }
    }
}