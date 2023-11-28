package io.github.pknujsp.weatherwizard.feature.notification.ongoing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.database.notification.model.OngoingNotificationInfoEntity
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.model.OngoingNotificationSettingsEntity
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.model.OngoingNotificationSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OngoingNotificationViewModel @Inject constructor(
    appSettingsRepository: SettingsRepository,
    private val dailyNotificationRepository: DailyNotificationRepository,
) : ViewModel() {
    val units = appSettingsRepository.currentUnits.value

    val notification = flow {
        val savedEntity = dailyNotificationRepository.getOngoingNotification()
        val valuesEntity = savedEntity.data.run {
            OngoingNotificationSettingsEntity().apply {
                locationType = getLocationType()
                refreshInterval = getRefreshInterval()
                weatherProvider = getWeatherProvider()
                notificationIconType = getNotificationIconType()
            }
        }

        emit(NotificationState(savedEntity.id, OngoingNotificationSettings(valuesEntity), savedEntity.enabled))
    }.flowOn(Dispatchers.IO).stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun switch(enabled: Boolean) {
        viewModelScope.launch {
            notification.value?.let {
                it.enabled = enabled
                if (it.hasHistory) {
                    updateNotificationInfo()
                }
            }
        }
    }

    fun updateNotificationInfo() {
        viewModelScope.launch {
            notification.value?.let { state ->
                val newEntity = state.uiModel.run {
                    val (latitude, longitude, addressName) = if (locationType is LocationType.CustomLocation) {
                        (locationType as LocationType.CustomLocation).run {
                            Triple(latitude, longitude, address)
                        }
                    } else {
                        Triple(0.0, 0.0, "")
                    }

                    NotificationSettingsEntity(state.idInDb.coerceAtLeast(0L), state.enabled,
                        io.github.pknujsp.weatherwizard.core.database.notification.model.OngoingNotificationInfoEntity(
                            latitude,
                            longitude,
                            addressName,
                            locationType.key,
                            refreshInterval.key,
                            weatherProvider.key,
                            notificationIconType.key,
                        ))
                }

                dailyNotificationRepository.setOngoingNotificationInfo(newEntity)

                state.hasHistory = true
                state.onChangedAction = if (state.enabled) NotificationState.NotificationAction.NOTIFY
                else NotificationState.NotificationAction.CANCEL
            }
        }
    }
}

class NotificationState(
    val idInDb: Long,
    val uiModel: OngoingNotificationSettings,
    enabled: Boolean,
) {
    var enabled: Boolean by mutableStateOf(enabled)

    var hasHistory = idInDb != -1L
    var onChangedAction by mutableStateOf(NotificationAction.NONE)

    enum class NotificationAction {
        NOTIFY, CANCEL, NONE
    }
}