package io.github.pknujsp.weatherwizard.feature.notification.daily.worker

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
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewModel
import io.github.pknujsp.weatherwizard.feature.notification.daily.model.DailyNotificationHeaderModel
import javax.inject.Inject

class DailyNotificationRemoteViewModel @Inject constructor(
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val dailyNotificationRepository: DailyNotificationRepository,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val nominatimRepository: NominatimRepository,
    appSettingsRepository: SettingsRepository,
) : RemoteViewModel() {

    val units = appSettingsRepository.currentUnits.value


    suspend fun loadNotification(notificationId: Long): DailyNotificationSettingsEntity {
        val notificationEntity = dailyNotificationRepository.getDailyNotification(notificationId)
        return notificationEntity.data
    }

    suspend fun load(
        dailyNotificationSettingsEntity: DailyNotificationSettingsEntity,
    ): DailyNotificationHeaderModel {
        return loadWeatherData(dailyNotificationSettingsEntity)
    }

    private suspend fun loadWeatherData(dailyNotificationSettingsEntity: DailyNotificationSettingsEntity): DailyNotificationHeaderModel {
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
                        return DailyNotificationHeaderModel(weatherDataRequest.requestedTime,
                            WeatherResponseState.Failure(-1, LocationTypeModel(), dailyNotificationSettingsEntity.weatherProvider),
                            dailyNotificationSettingsEntity)
                    })

                }

                else -> {
                    return DailyNotificationHeaderModel(weatherDataRequest.requestedTime,
                        WeatherResponseState.Failure(-1, LocationTypeModel(), dailyNotificationSettingsEntity.weatherProvider),
                        dailyNotificationSettingsEntity)
                }
            }
        } else {
            weatherDataRequest.addRequest(
                dailyNotificationSettingsEntity.location,
                dailyNotificationSettingsEntity.type.categories.toSet(),
                dailyNotificationSettingsEntity.weatherProvider,
            )
        }


        val response = getWeatherDataUseCase(weatherDataRequest.requests[0])

        val uiModel = DailyNotificationHeaderModel(weatherDataRequest.requestedTime,
            response,
            notification = if (dailyNotificationSettingsEntity.location.locationType is LocationType.CurrentLocation) {
                dailyNotificationSettingsEntity.copy(location = dailyNotificationSettingsEntity.location.copy(
                    latitude = response.location.latitude,
                    longitude = response.location.longitude,
                    address = response.location.address,
                    country = response.location.country,
                ))
            } else {
                dailyNotificationSettingsEntity
            })

        return uiModel
    }

}