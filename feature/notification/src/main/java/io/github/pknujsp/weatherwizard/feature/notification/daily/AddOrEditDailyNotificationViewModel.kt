package io.github.pknujsp.weatherwizard.feature.notification.daily

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.model.notification.daily.DailySavedNotificationInfo
import io.github.pknujsp.weatherwizard.core.data.notification.daily.model.DailyNotificationSettingsJsonEntity
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddOrEditDailyNotificationViewModel @Inject constructor(
    private val dailyNotificationRepository: DailyNotificationRepository,
    savedStateHandle: SavedStateHandle,
    appSettingsRepository: SettingsRepository
) : ViewModel() {

    val units = appSettingsRepository.currentUnits

    private val _notification = MutableStateFlow<UiState<DailySavedNotificationInfo>>(UiState.Loading)
    val notification: StateFlow<UiState<DailySavedNotificationInfo>> = _notification

    init {
        viewModelScope.launch {
            val id = savedStateHandle.get<Long>("id")!!
            val info = dailyNotificationRepository.getDailyNotification(id).let {
                DailySavedNotificationInfo(
                    latitude = it.data.latitude,
                    longitude = it.data.longitude,
                    hour = it.data.hour,
                    minute = it.data.minute,
                    enabled = it.enabled,
                    locationType = it.data.getLocationType(),
                    weatherProvider = it.data.getWeatherProvider(),
                    type = it.data.getType(),
                    id = it.id,
                    addressName = it.data.addressName,
                )
            }
            _notification.value = UiState.Success(info)
        }
    }

    fun save() {
        viewModelScope.launch {
            notification.value.onSuccess { info ->
                val entity =
                    NotificationSettingsEntity(
                        id = info.id.coerceAtLeast(0L),
                        enabled = info.enabled,
                        data = DailyNotificationSettingsJsonEntity(
                            latitude = info.latitude,
                            longitude = info.longitude,
                            hour = info.hour,
                            minute = info.minute,
                            addressName = info.addressName,
                            locationType = info.locationType.key,
                            weatherProvider = info.weatherProvider.key,
                            type = info.type.key,
                        )
                    )
                val id = dailyNotificationRepository.updateDailyNotification(entity)
                info.id = id
                info.onSaved = true
            }
        }
    }
}