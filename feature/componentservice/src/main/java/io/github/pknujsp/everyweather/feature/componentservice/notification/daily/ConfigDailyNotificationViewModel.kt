package io.github.pknujsp.everyweather.feature.componentservice.notification.daily

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.everyweather.core.data.notification.daily.model.DailyNotificationSettingsEntity
import io.github.pknujsp.everyweather.core.data.settings.SettingsRepository
import io.github.pknujsp.everyweather.core.model.notification.NotificationSettingsEntity
import io.github.pknujsp.everyweather.feature.componentservice.notification.NotificationRoutes
import io.github.pknujsp.everyweather.feature.componentservice.notification.daily.model.DailyNotificationSettings
import io.github.pknujsp.everyweather.feature.componentservice.notification.daily.model.DailyNotificationUiState
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ConfigDailyNotificationViewModel
    @Inject
    constructor(
        private val dailyNotificationRepository: DailyNotificationRepository,
        @CoDispatcher(CoDispatcherType.IO) private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher,
        private val savedStateHandle: SavedStateHandle,
        private val appSettingsRepository: SettingsRepository,
    ) : ViewModel() {
        private var notificationId = savedStateHandle.get<Long>(NotificationRoutes.AddOrEditDaily.arguments.first().name)!!
        private val isNew get() = notificationId == -1L

        val units get() = appSettingsRepository.settings.replayCache.last().units

        private val _dailyNoficationUiState =
            MutableDailyNotificationUiState(
                dailyNotificationSettings =
                    DailyNotificationSettingsEntity().let {
                        DailyNotificationSettings(
                            type = it.type,
                            location = it.location,
                            hour = it.hour,
                            minute = it.minute,
                            weatherProvider = it.weatherProvider,
                        )
                    },
                update = ::update,
                isNew = isNew,
            )

        val dailyNotificationUiState: DailyNotificationUiState = _dailyNoficationUiState

        init {
            viewModelScope.launch {
                if (!isNew) {
                    withContext(ioDispatcher) { dailyNotificationRepository.getDailyNotification(notificationId) }.run {
                        _dailyNoficationUiState.dailyNotificationSettings =
                            DailyNotificationSettings(
                                type = data.type,
                                location = data.location,
                                hour = data.hour,
                                minute = data.minute,
                                weatherProvider = data.weatherProvider,
                            )
                        _dailyNoficationUiState.isEnabled = enabled
                    }
                }
            }
        }

        private fun createSettingsEntity() =
            dailyNotificationUiState.dailyNotificationSettings.let {
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
                _dailyNoficationUiState.isEnabled = dailyNotificationUiState.isEnabled || isNew

                withContext(ioDispatcher) {
                    val settingsEntity =
                        NotificationSettingsEntity(
                            id = if (isNew) 0 else notificationId,
                            enabled = dailyNotificationUiState.isEnabled,
                            data = createSettingsEntity(),
                            isInitialized = true,
                        )
                    val updatedNotificationId =
                        dailyNotificationRepository.run {
                            if (isNew) {
                                createDailyNotification(settingsEntity)
                            } else {
                                updateDailyNotification(settingsEntity)
                                notificationId
                            }
                        }
                    updatedNotificationId
                }.let { updatedNotificationId ->
                    notificationId = updatedNotificationId
                    savedStateHandle[NotificationRoutes.AddOrEditDaily.arguments.first().name] = updatedNotificationId
                }
                _dailyNoficationUiState.action = DailyNotificationUiState.Action.UPDATED(notificationId)
            }
        }
    }

private class MutableDailyNotificationUiState(
    dailyNotificationSettings: DailyNotificationSettings,
    private val update: () -> Unit,
    override val isNew: Boolean,
) : DailyNotificationUiState {
    override var dailyNotificationSettings by mutableStateOf(dailyNotificationSettings)
    override var isEnabled by mutableStateOf(false)
    override var action: DailyNotificationUiState.Action by mutableStateOf(DailyNotificationUiState.Action.NONE)

    override fun update() {
        update.invoke()
    }
}
