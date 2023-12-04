package io.github.pknujsp.weatherwizard.feature.notification.daily.worker

import io.github.pknujsp.weatherwizard.core.data.notification.daily.DailyNotificationRepository
import io.github.pknujsp.weatherwizard.core.data.notification.daily.model.DailyNotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.domain.location.CurrentLocationResultState
import io.github.pknujsp.weatherwizard.core.domain.location.GetCurrentLocationUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetWeatherDataUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.coordinate.Coordinate
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.feature.notification.daily.model.forecast.DailyNotificationForecastUiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewModel
import io.github.pknujsp.weatherwizard.feature.notification.daily.DailyNotificationDataMapperManager
import javax.inject.Inject

class DailyNotificationRemoteViewModel @Inject constructor(
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val dailyNotificationRepository: DailyNotificationRepository,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val appSettingsRepository: SettingsRepository,
) : RemoteViewModel() {
    suspend fun load(notificationId: Long) {
        val notificationEntity = dailyNotificationRepository.getDailyNotification(notificationId)
        val location = loadAddress(notificationEntity.data.locationType)

        if (location.isSuccess) {
            val request = WeatherDataRequest()
            request.addRequest(location.getOrThrow(),
                notificationEntity.data.type.categories.toSet(),
                notificationEntity.data.weatherProvider)
            val response = getWeatherDataUseCase(request.requests[0])
            val mapper = DailyNotificationDataMapperManager.getMapperByType(notificationEntity.data.type)
            val uiModel = mapper.map(response)
        }


    }


    private suspend fun loadAddress(locationType: LocationType): Result<LocationModel> = when (locationType) {
        is LocationType.CustomLocation -> {
            Result.success(LocationModel(locationType.latitude, locationType.longitude, locationType.address))
        }

        is LocationType.CurrentLocation -> {
            when (val result = getCurrentLocationUseCase()) {
                is CurrentLocationResultState.Success -> {
                    Result.success(result.location)
                }

                is CurrentLocationResultState.Failure -> {
                    Result.failure(Throwable("Location is null"))
                }
            }
        }
    }

}