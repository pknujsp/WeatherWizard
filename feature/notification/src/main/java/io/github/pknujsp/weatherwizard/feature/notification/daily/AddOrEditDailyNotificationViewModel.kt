package io.github.pknujsp.weatherwizard.feature.notification.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.notification.NotificationRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationEntity
import io.github.pknujsp.weatherwizard.core.model.notification.daily.DailyNotificationInfo
import io.github.pknujsp.weatherwizard.core.model.notification.daily.DailyNotificationInfoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddOrEditDailyNotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    appSettingsRepository: SettingsRepository
) : ViewModel() {

    val units = appSettingsRepository.currentUnits

    private val _notification = MutableStateFlow<DailyNotificationInfo?>(null)
    val notification: StateFlow<DailyNotificationInfo?> = _notification

    private val _savedId = MutableStateFlow<Long?>(null)
    val savedId: StateFlow<Long?> = _savedId

    fun load(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _notification.value = notificationRepository.getDailyNotification(id).let {
                DailyNotificationInfo(
                    latitude = it.data.latitude,
                    longitude = it.data.longitude,
                    hour = it.data.hour,
                    minute = it.data.minute,
                    enabled = it.enabled,
                    locationType = it.data.getLocationType(),
                    weatherProvider = it.data.getWeatherProvider(),
                    type = it.data.getType(),
                    id = it.idInDb,
                    addressName = it.data.addressName,
                )
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            notification.value?.let {
                _savedId.value = notificationRepository.setDailyNotificationInfo(
                    NotificationEntity(
                        idInDb = it.id.coerceAtLeast(0L),
                        enabled = it.enabled,
                        data = DailyNotificationInfoEntity(
                            latitude = it.latitude,
                            longitude = it.longitude,
                            hour = it.hour,
                            minute = it.minute,
                            addressName = it.addressName,
                            locationType = it.locationType.key,
                            weatherProvider = it.weatherProvider.key,
                            type = it.type.key,
                        )
                    )
                )
            }
        }
    }
}