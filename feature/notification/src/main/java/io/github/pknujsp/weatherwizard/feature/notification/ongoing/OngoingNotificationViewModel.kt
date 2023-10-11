package io.github.pknujsp.weatherwizard.feature.notification.ongoing

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.IRadioButton
import io.github.pknujsp.weatherwizard.core.data.favorite.FavoriteAreaListRepository
import io.github.pknujsp.weatherwizard.core.data.notification.NotificationRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationIconType
import io.github.pknujsp.weatherwizard.core.model.notification.OngoingNotificationInfoEntity
import io.github.pknujsp.weatherwizard.core.model.notification.RefreshInterval
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val favoriteAreaListRepository: FavoriteAreaListRepository
) : ViewModel() {
    val units: StateFlow<CurrentUnits> = appSettingsRepository.currentUnits

    private val _locationType: MutableStateFlow<LocationType> = MutableStateFlow(LocationType.CurrentLocation)
    val locationType: StateFlow<LocationType> = _locationType

    private val _weatherProvider: MutableStateFlow<WeatherDataProvider> = MutableStateFlow(WeatherDataProvider.default)
    val weatherProvider: StateFlow<WeatherDataProvider> = _weatherProvider

    private val _refreshInterval: MutableStateFlow<RefreshInterval> = MutableStateFlow(RefreshInterval.HOUR_1)
    val refreshInterval: StateFlow<RefreshInterval> = _refreshInterval

    private val _notificationIcon: MutableStateFlow<NotificationIconType> = MutableStateFlow(NotificationIconType.TEMPERATURE)
    val notificationIcon: StateFlow<NotificationIconType> = _notificationIcon

    private var newEntity: OngoingNotificationInfoEntity by Delegates.notNull()

    val notificationState = flow {
        val savedEntity = notificationRepository.getOngoingNotificationInfo().getOrNull()
        emit(NotificationState(savedEntity ?: OngoingNotificationInfoEntity(), savedEntity != null).apply {
            newEntity = entity
        })
    }.flowOn(Dispatchers.IO).stateIn(viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = NotificationState(OngoingNotificationInfoEntity(), false))


    init {
        viewModelScope.launch {
            val favoriteAreaListEntity = favoriteAreaListRepository.getAll()
            notificationState.collect { status ->
                when (status.enabled) {
                    true -> {
                        status.entity.run {
                            _locationType.value = if (latitude == 0.0 && longitude == 0.0)
                                LocationType.CurrentLocation
                            else
                                LocationType.CustomLocation(latitude, longitude)

                            _weatherProvider.value = getWeatherProvider()
                            _refreshInterval.value = getAutoRefreshInterval()
                            _notificationIcon.value = getNotificationIconType()
                        }

                    }

                    else -> {}
                }
            }
        }
    }
}

class NotificationState(
    val entity: OngoingNotificationInfoEntity,
    enabled: Boolean
) {
    var enabled by mutableStateOf(enabled)
}

sealed class LocationType(@StringRes override val title: Int) : IRadioButton {

    companion object {
        val types get() = arrayOf(CurrentLocation, CustomLocation(0.0, 0.0))
    }

    class CustomLocation(val latitude: Double, val longitude: Double) :
        LocationType(io.github.pknujsp.weatherwizard.core.common.R.string.custom_location)

    data object CurrentLocation : LocationType(io.github.pknujsp.weatherwizard.core.common.R.string.current_location)
}