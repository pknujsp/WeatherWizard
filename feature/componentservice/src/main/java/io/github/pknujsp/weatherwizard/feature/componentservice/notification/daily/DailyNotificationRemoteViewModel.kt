package io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily

import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.notification.daily.model.DailyNotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.domain.location.CurrentLocationState
import io.github.pknujsp.weatherwizard.core.domain.location.GetCurrentLocationUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetWeatherDataUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.DailyNotificationRemoteViewUiState
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewModel
import javax.inject.Inject

class DailyNotificationRemoteViewModel @Inject constructor(
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val dailyNotificationRepository: DailyNotificationRepository,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val nominatimRepository: NominatimRepository,
    appSettingsRepository: SettingsRepository,
) : RemoteViewModel() {

    val units = appSettingsRepository.settings.replayCache.last().units


    suspend fun loadNotification(notificationId: Long): DailyNotificationSettingsEntity {
        val notificationEntity = dailyNotificationRepository.getDailyNotification(notificationId)
        return notificationEntity.data
    }

    suspend fun load(
        dailyNotificationSettingsEntity: DailyNotificationSettingsEntity,
    ): DailyNotificationRemoteViewUiState {
        return loadWeatherData(dailyNotificationSettingsEntity)
    }

    private suspend fun loadWeatherData(dailyNotificationSettingsEntity: DailyNotificationSettingsEntity): DailyNotificationRemoteViewUiState {
        val weatherDataRequest = WeatherDataRequest()
        var addressName: String? = null

        if (dailyNotificationSettingsEntity.location.locationType is LocationType.CurrentLocation) {
            when (val currentLocation = getCurrentLocationUseCase()) {
                is CurrentLocationState.Success -> {
                    val address = nominatimRepository.reverseGeoCode(currentLocation.latitude, currentLocation.longitude)
                    address.fold(onSuccess = {
                        addressName = it.simpleDisplayName
                        weatherDataRequest.addRequest(
                            WeatherDataRequest.Coordinate(currentLocation.latitude, currentLocation.longitude),
                            dailyNotificationSettingsEntity.type.categories.toSet(),
                            dailyNotificationSettingsEntity.weatherProvider,
                        )
                    }, onFailure = {
                        return DailyNotificationRemoteViewUiState(
                            isSuccessful = false,
                            notificationType = dailyNotificationSettingsEntity.type,
                        )
                    })

                }

                else -> {
                    return DailyNotificationRemoteViewUiState(
                        isSuccessful = false,
                        notificationType = dailyNotificationSettingsEntity.type,
                    )
                }
            }
        } else {
            addressName = dailyNotificationSettingsEntity.location.address
            weatherDataRequest.addRequest(
                WeatherDataRequest.Coordinate(
                    dailyNotificationSettingsEntity.location.latitude,
                    dailyNotificationSettingsEntity.location.longitude,
                ),
                dailyNotificationSettingsEntity.type.categories.toSet(),
                dailyNotificationSettingsEntity.weatherProvider,
            )
        }

        return when (val response = getWeatherDataUseCase(weatherDataRequest.finalRequests[0], false)) {
            is WeatherResponseState.Success -> DailyNotificationRemoteViewUiState(model = response.entity,
                address = addressName,
                lastUpdated = weatherDataRequest.requestedTime,
                notificationType = dailyNotificationSettingsEntity.type,
                isSuccessful = true)

            else -> DailyNotificationRemoteViewUiState(
                isSuccessful = false,
                notificationType = dailyNotificationSettingsEntity.type,
            )
        }
    }

}