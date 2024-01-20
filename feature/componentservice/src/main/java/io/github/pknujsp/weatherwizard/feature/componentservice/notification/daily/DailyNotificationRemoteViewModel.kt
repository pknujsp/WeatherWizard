package io.github.pknujsp.weatherwizard.feature.componentservice.notification.daily

import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.notification.daily.model.DailyNotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.domain.location.GetCurrentLocationAddress
import io.github.pknujsp.weatherwizard.core.domain.weather.GetWeatherDataUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.daily.DailyNotificationRemoteViewUiState
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DailyNotificationRemoteViewModel @Inject constructor(
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val dailyNotificationRepository: DailyNotificationRepository,
    private val appSettingsRepository: SettingsRepository,
    getCurrentLocationUseCase: GetCurrentLocationAddress,
) : RemoteViewModel(getCurrentLocationUseCase) {

    val units get() = appSettingsRepository.settings.replayCache.last().units


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
        val weatherDataRequestBuilder = WeatherDataRequest.Builder()
        if (dailyNotificationSettingsEntity.location.locationType is LocationType.CurrentLocation) {
            getCurrentLocation().first().let {
                if (it is CurrentLocationResult.Success) {
                    weatherDataRequestBuilder.add(
                        WeatherDataRequest.Coordinate(
                            it.latitude,
                            it.longitude,
                            it.address,
                        ),
                        dailyNotificationSettingsEntity.type.categories,
                        dailyNotificationSettingsEntity.weatherProvider,
                    )
                }
            }
        } else {
            weatherDataRequestBuilder.add(
                WeatherDataRequest.Coordinate(
                    dailyNotificationSettingsEntity.location.latitude,
                    dailyNotificationSettingsEntity.location.longitude,
                    dailyNotificationSettingsEntity.location.address,
                ),
                dailyNotificationSettingsEntity.type.categories,
                dailyNotificationSettingsEntity.weatherProvider,
            )
        }

        return when (val response = getWeatherDataUseCase(weatherDataRequestBuilder.build()[0], false)) {
            is WeatherResponseState.Success -> DailyNotificationRemoteViewUiState(model = response.entity,
                address = response.location.address,
                lastUpdated = response.entity.responseTime,
                notificationType = dailyNotificationSettingsEntity.type,
                isSuccessful = true)

            else -> DailyNotificationRemoteViewUiState(
                isSuccessful = false,
                notificationType = dailyNotificationSettingsEntity.type,
            )
        }
    }


}