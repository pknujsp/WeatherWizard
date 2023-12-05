package io.github.pknujsp.weatherwizard.feature.notification.ongoing.worker

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
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewModel
import io.github.pknujsp.weatherwizard.feature.notification.ongoing.model.OngoingNotificationHeaderModel
import javax.inject.Inject

class OngoingNotificationRemoteViewModel @Inject constructor(
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val ongoingNotificationRepository: OngoingNotificationRepository,
    private val nominatimRepository: NominatimRepository,
    appSettingsRepository: SettingsRepository,
) : RemoteViewModel() {

    val units = appSettingsRepository.currentUnits.value

    suspend fun loadNotification(): OngoingNotificationSettingsEntity {
        val notificationEntity = ongoingNotificationRepository.getOngoingNotification()
        return notificationEntity.data
    }

    suspend fun load(
        settings: OngoingNotificationSettingsEntity,
    ): OngoingNotificationHeaderModel {
        return loadWeatherData(settings)
    }

    private suspend fun loadWeatherData(settings: OngoingNotificationSettingsEntity): OngoingNotificationHeaderModel {
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
                        return OngoingNotificationHeaderModel(weatherDataRequest.requestedTime,
                            WeatherResponseState.Failure(-1, LocationTypeModel(), settings.weatherProvider),
                            settings)
                    })
                }

                else -> {
                    return OngoingNotificationHeaderModel(weatherDataRequest.requestedTime,
                        WeatherResponseState.Failure(-1, LocationTypeModel(), settings.weatherProvider),
                        settings)
                }
            }
        } else {
            weatherDataRequest.addRequest(
                settings.location,
                settings.type.categories.toSet(),
                settings.weatherProvider,
            )
        }

        val response = getWeatherDataUseCase(weatherDataRequest.requests[0])

        val uiModel = OngoingNotificationHeaderModel(weatherDataRequest.requestedTime,
            response,
            notification = if (settings.location.locationType is LocationType.CurrentLocation) {
                settings.copy(location = response.location)
            } else {
                settings
            })

        return uiModel
    }
}