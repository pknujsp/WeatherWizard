package io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.worker

import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.notification.daily.model.DailyNotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.domain.location.CurrentLocationResultState
import io.github.pknujsp.weatherwizard.core.domain.location.GetCurrentLocationUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetWeatherDataUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import javax.inject.Inject

class DailyNotificationRemoteViewModel @Inject constructor(
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val dailyNotificationRepository: DailyNotificationRepository,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val nominatimRepository: NominatimRepository,
    appSettingsRepository: SettingsRepository,
) : io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewModel() {

    val units = appSettingsRepository.currentUnits.value


    suspend fun loadNotification(notificationId: Long): DailyNotificationSettingsEntity {
        val notificationEntity = dailyNotificationRepository.getDailyNotification(notificationId)
        return notificationEntity.data
    }

    suspend fun load(
        dailyNotificationSettingsEntity: DailyNotificationSettingsEntity,
    ): io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.DailyNotificationRemoteViewUiState {
        return loadWeatherData(dailyNotificationSettingsEntity)
    }

    private suspend fun loadWeatherData(dailyNotificationSettingsEntity: DailyNotificationSettingsEntity): io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.DailyNotificationRemoteViewUiState {
        val weatherDataRequest = WeatherDataRequest()

        if (dailyNotificationSettingsEntity.location.locationType is LocationType.CurrentLocation) {
            when (val currentLocation = getCurrentLocationUseCase()) {
                is CurrentLocationResultState.Success -> {
                    val address = nominatimRepository.reverseGeoCode(currentLocation.latitude, currentLocation.longitude)
                    address.fold(onSuccess = {
                        weatherDataRequest.addRequest(
                            dailyNotificationSettingsEntity.location.copy(
                                address = it.simpleDisplayName,
                                country = it.country,
                                latitude = currentLocation.latitude,
                                longitude = currentLocation.longitude,
                            ),
                            dailyNotificationSettingsEntity.type.categories.toSet(),
                            dailyNotificationSettingsEntity.weatherProvider,
                        )
                    }, onFailure = {
                        return io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.DailyNotificationRemoteViewUiState(
                            isSuccessful = false,
                            notificationType = dailyNotificationSettingsEntity.type,
                        )
                    })

                }

                else -> {
                    return io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.DailyNotificationRemoteViewUiState(
                        isSuccessful = false,
                        notificationType = dailyNotificationSettingsEntity.type,
                    )
                }
            }
        } else {
            weatherDataRequest.addRequest(
                dailyNotificationSettingsEntity.location,
                dailyNotificationSettingsEntity.type.categories.toSet(),
                dailyNotificationSettingsEntity.weatherProvider,
            )
        }

        return when (val response = getWeatherDataUseCase(weatherDataRequest.finalRequests[0], false)) {
            is WeatherResponseState.Success -> io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.DailyNotificationRemoteViewUiState(
                model = response.entity,
                address = response.location.address,
                lastUpdated = weatherDataRequest.requestedTime,
                notificationType = dailyNotificationSettingsEntity.type,
                isSuccessful = true)

            else -> io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.DailyNotificationRemoteViewUiState(
                isSuccessful = false,
                notificationType = dailyNotificationSettingsEntity.type,
            )
        }
    }

}