package io.github.pknujsp.weatherwizard.feature.notification.ongoing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.data.notification.NotificationRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationEntity
import io.github.pknujsp.weatherwizard.core.model.notification.OngoingNotificationInfo
import io.github.pknujsp.weatherwizard.core.model.notification.OngoingNotificationInfoEntity
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class OngoingNotificationViewModel @Inject constructor(
    appSettingsRepository: SettingsRepository,
    private val notificationRepository: NotificationRepository,
) : ViewModel() {
    val units: StateFlow<CurrentUnits> = appSettingsRepository.currentUnits

    private var originalNotificationInfo: OngoingNotificationInfo by Delegates.notNull()

    val notificationState = flow {
        val savedEntity = notificationRepository.getOngoingNotificationInfo()
        emit(NotificationState(savedEntity.idInDb, savedEntity.data.run {
            val info = OngoingNotificationInfo(latitude, longitude, addressName, getLocationType(), getRefreshInterval(),
                getWeatherProvider(), getNotificationIconType())
            originalNotificationInfo = info.copy()

            info
        }, savedEntity.enabled))
    }.flowOn(Dispatchers.IO).stateIn(viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = NotificationState(-1L, OngoingNotificationInfo(), false))

    fun switch() {
        viewModelScope.launch {
            with(notificationState.value) {
                if (hasHistory) {
                    updateNotificationInfo()
                }
            }
        }
    }

    fun updateNotificationInfo() {
        viewModelScope.launch {
            val state = notificationState.value
            val newEntity = (if (state.enabled) state.info else originalNotificationInfo).run {
                NotificationEntity(state.idInDb.coerceAtLeast(0L), state.enabled,
                    OngoingNotificationInfoEntity(latitude, longitude, addressName, locationType
                        .key, refreshInterval.key, weatherProvider.key, notificationIconType.key))
            }

            notificationRepository.setOngoingNotificationInfo(newEntity)
            state.hasHistory = true
            state.onChangedAction = if (state.enabled) NotificationState.NotificationAction.NOTIFY
            else NotificationState.NotificationAction.CANCEL
        }
    }
}

class NotificationState(
    val idInDb: Long,
    val info: OngoingNotificationInfo,
    enabled: Boolean
) {
    var hasHistory: Boolean = idInDb != 1L
    var enabled by mutableStateOf(enabled)
    var onChangedAction by mutableStateOf(NotificationAction.NONE)

    enum class NotificationAction {
        NOTIFY, CANCEL, NONE
    }
}