package io.github.pknujsp.weatherwizard.core.widgetnotification.notification.ongoing.worker

import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.OngoingNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.model.OngoingNotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.domain.location.CurrentLocationResultState
import io.github.pknujsp.weatherwizard.core.domain.location.GetCurrentLocationUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetWeatherDataUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.ongoing.model.OngoingNotificationRemoteViewUiState
import javax.inject.Inject

class OngoingNotificationRemoteViewModel @Inject constructor(
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val ongoingNotificationRepository: OngoingNotificationRepository,
    private val nominatimRepository: NominatimRepository,
    appSettingsRepository: SettingsRepository,
) : io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewModel() {

    val units = appSettingsRepository.currentUnits.value

    suspend fun loadNotification(): OngoingNotificationSettingsEntity {
        val notificationEntity = ongoingNotificationRepository.getOngoingNotification()
        return notificationEntity.data
    }

    suspend fun load(
        settings: OngoingNotificationSettingsEntity,
    ): OngoingNotificationRemoteViewUiState = loadWeatherData(settings)

    private suspend fun loadWeatherData(settings: OngoingNotificationSettingsEntity): OngoingNotificationRemoteViewUiState {
        val weatherDataRequest = WeatherDataRequest()
        if (settings.location.locationType is LocationType.CurrentLocation) {
            when (val currentLocation = getCurrentLocationUseCase()) {
                is CurrentLocationResultState.Success -> {
                    nominatimRepository.reverseGeoCode(currentLocation.latitude, currentLocation.longitude).fold(onSuccess = {
                        weatherDataRequest.addRequest(
                            settings.location.copy(
                                address = it.simpleDisplayName,
                                country = it.country,
                                latitude = currentLocation.latitude,
                                longitude = currentLocation.longitude,
                            ),
                            settings.type.categories.toSet(),
                            settings.weatherProvider,
                        )
                    }, onFailure = {
                        return OngoingNotificationRemoteViewUiState(isSuccessful = false, notificationType = settings.type)
                    })
                }

                else -> {
                    return OngoingNotificationRemoteViewUiState(isSuccessful = false, notificationType = settings.type)
                }
            }
        } else {
            weatherDataRequest.addRequest(
                settings.location,
                settings.type.categories.toSet(),
                settings.weatherProvider,
            )
        }

        return when (val response = getWeatherDataUseCase(weatherDataRequest.finalRequests[0], false)) {
            is WeatherResponseState.Success -> OngoingNotificationRemoteViewUiState(notificationIconType = settings.notificationIconType,
                model = response.entity,
                address = response.location.address,
                lastUpdated = weatherDataRequest.requestedTime,
                notificationType = settings.type,
                isSuccessful = true)

            else -> OngoingNotificationRemoteViewUiState(isSuccessful = false, notificationType = settings.type)
        }
    }
}