package io.github.pknujsp.weatherwizard.feature.componentservice.notification.ongoing

import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.OngoingNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.model.OngoingNotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.domain.location.GetCurrentLocationAddress
import io.github.pknujsp.weatherwizard.core.domain.weather.GetWeatherDataUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequestBuilder
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.ongoing.OngoingNotificationRemoteViewUiState
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class OngoingNotificationRemoteViewModel @Inject constructor(
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

    suspend fun load(
        settings: OngoingNotificationSettingsEntity,
    ): OngoingNotificationRemoteViewUiState = loadWeatherData(settings)

    private suspend fun loadWeatherData(settings: OngoingNotificationSettingsEntity): OngoingNotificationRemoteViewUiState {
        val weatherDataRequestBuilder = WeatherDataRequestBuilder()
        val addressName: String?

        if (settings.location.locationType is LocationType.CurrentLocation) {
            when (val currentLocation = getCurrentLocation().first()) {
                is CurrentLocationResult.Success -> {
                    addressName = currentLocation.address
                    weatherDataRequestBuilder.add(
                        WeatherDataRequestBuilder.Coordinate(
                            latitude = currentLocation.latitude,
                            longitude = currentLocation.longitude,
                        ),
                        settings.type.categories.toSet(),
                        settings.weatherProvider,
                    )
                }

                else -> {
                    return OngoingNotificationRemoteViewUiState(isSuccessful = false, notificationType = settings.type)
                }
            }
        } else {
            addressName = settings.location.address
            weatherDataRequestBuilder.add(
                settings.location.run {
                    WeatherDataRequestBuilder.Coordinate(
                        latitude = latitude,
                        longitude = longitude,
                    )
                },
                settings.type.categories.toSet(),
                settings.weatherProvider,
            )
        }

        return when (val response = getWeatherDataUseCase(weatherDataRequestBuilder.finalRequests[0], false)) {
            is WeatherResponseState.Success -> OngoingNotificationRemoteViewUiState(notificationIconType = settings.notificationIconType,
                model = response.entity,
                address = addressName,
                lastUpdated = weatherDataRequestBuilder.time,
                notificationType = settings.type,
                isSuccessful = true)

            else -> OngoingNotificationRemoteViewUiState(isSuccessful = false, notificationType = settings.type)
        }
    }
}