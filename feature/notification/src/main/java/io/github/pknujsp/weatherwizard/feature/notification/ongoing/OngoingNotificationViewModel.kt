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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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

    val notification = flow {
        val savedEntity = notificationRepository.getOngoingNotification()
        emit(NotificationState(savedEntity.idInDb, savedEntity.data.run {
            val info = OngoingNotificationInfo(latitude, longitude, addressName, getLocationType(), getRefreshInterval(),
                getWeatherProvider(), getNotificationIconType())
            originalNotificationInfo = info.copy()

            info
        }, savedEntity.enabled))
    }.flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

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
            val state = notification.value!!
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
    enabled: Boolean,
) {
    var enabled: Boolean by mutableStateOf(enabled)

    var hasHistory = idInDb != -1L
    var onChangedAction by mutableStateOf(NotificationAction.NONE)

    enum class NotificationAction {
        NOTIFY, CANCEL, NONE
    }
}