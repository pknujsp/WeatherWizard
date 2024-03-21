package io.github.pknujsp.everyweather.feature.componentservice.notification.ongoing

import io.github.pknujsp.everyweather.core.data.notification.ongoing.OngoingNotificationRepository
import io.github.pknujsp.everyweather.core.data.notification.ongoing.model.OngoingNotificationSettingsEntity
import io.github.pknujsp.everyweather.core.data.settings.SettingsRepository
import io.github.pknujsp.everyweather.core.domain.location.GetCurrentLocationAddress
import io.github.pknujsp.everyweather.core.domain.weather.GetWeatherDataUseCase
import io.github.pknujsp.everyweather.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.everyweather.core.domain.weather.WeatherResponseState
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.widgetnotification.notification.ongoing.OngoingNotificationRemoteViewUiState
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.RemoteViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class OngoingNotificationRemoteViewModel
    @Inject
    constructor(
        private val getWeatherDataUseCase: GetWeatherDataUseCase,
        private val ongoingNotificationRepository: OngoingNotificationRepository,
        private val appSettingsRepository: SettingsRepository,
        getCurrentLocationUseCase: GetCurrentLocationAddress,
    ) : RemoteViewModel(getCurrentLocationUseCase) {
        val units get() = appSettingsRepository.settings.replayCache.last().units

        suspend fun loadNotification(): OngoingNotificationSettingsEntity {
            val notificationEntity = ongoingNotificationRepository.getOngoingNotification()
            return notificationEntity.data
        }

        suspend fun load(settings: OngoingNotificationSettingsEntity): OngoingNotificationRemoteViewUiState = loadWeatherData(settings)

        private suspend fun loadWeatherData(settings: OngoingNotificationSettingsEntity): OngoingNotificationRemoteViewUiState {
            val weatherDataRequestBuilder = WeatherDataRequest.Builder()

            if (settings.location.locationType is LocationType.CurrentLocation) {
                when (val currentLocation = getCurrentLocation().first()) {
                    is CurrentLocationResult.Success -> {
                        weatherDataRequestBuilder.add(
                            WeatherDataRequest.Coordinate(
                                latitude = currentLocation.latitude,
                                longitude = currentLocation.longitude,
                                address = currentLocation.address,
                            ),
                            settings.type.categories,
                            settings.weatherProvider,
                        )
                    }

                    else -> {
                        return OngoingNotificationRemoteViewUiState(isSuccessful = false, notificationType = settings.type)
                    }
                }
            } else {
                weatherDataRequestBuilder.add(
                    settings.location.run {
                        WeatherDataRequest.Coordinate(
                            latitude = latitude,
                            longitude = longitude,
                            address = address,
                        )
                    },
                    settings.type.categories,
                    settings.weatherProvider,
                )
            }

            return when (val response = getWeatherDataUseCase(weatherDataRequestBuilder.build()[0], false)) {
                is WeatherResponseState.Success ->
                    OngoingNotificationRemoteViewUiState(
                        notificationIconType = settings.notificationIconType,
                        model = response.entity,
                        address = response.location.address,
                        lastUpdated = response.entity.responseTime,
                        notificationType = settings.type,
                        isSuccessful = true,
                    )

                else -> OngoingNotificationRemoteViewUiState(isSuccessful = false, notificationType = settings.type)
            }
        }
    }
