package io.github.pknujsp.weatherwizard.feature.notification.ongoing.worker

import io.github.pknujsp.weatherwizard.core.common.UnavailableFeature
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.notification.NotificationRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.domain.weather.GetCurrentWeatherUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetHourlyForecastUseCase
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.ongoing.OngoingNotificationUiModel
import io.github.pknujsp.weatherwizard.feature.notification.common.RemoteViewModel
import java.time.ZonedDateTime
import javax.inject.Inject

class OngoingNotificationRemoteViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getHourlyForecastUseCase: GetHourlyForecastUseCase,
    private val appSettingsRepository: SettingsRepository,
    private val notificationRepository: NotificationRepository,
    private val nominatimRepository: NominatimRepository,
) : RemoteViewModel() {

    suspend fun load(): UiState<OngoingNotificationUiModel> {
        appSettingsRepository.init()
        val units = appSettingsRepository.currentUnits.value
        val notificationInfo = notificationRepository.getOngoingNotification()

        val now = ZonedDateTime.now()
        val requestId = now.toInstant().toEpochMilli()

        val latitude = notificationInfo.data.latitude
        val longitude = notificationInfo.data.longitude
        val locationType = notificationInfo.data.getLocationType()
        val address = if (locationType is LocationType.CurrentLocation) nominatimRepository.reverseGeoCode(latitude, longitude)
            .getOrThrow().simpleDisplayName
        else notificationInfo.data.addressName

        val weatherProvider = notificationInfo.data.getWeatherProvider()

        val currentWeather =
            getCurrentWeatherUseCase(latitude, longitude, weatherProvider, requestId).getOrNull()
        val hourlyForecast =
            getHourlyForecastUseCase(latitude, longitude, weatherProvider, requestId).getOrNull()

        return if (currentWeather != null && hourlyForecast != null) {
            val dayNightCalculator = DayNightCalculator(latitude, longitude)

            UiState.Success(OngoingNotificationUiModel(
                address = address,
                currentWeather = currentWeather,
                hourlyForecast = hourlyForecast,
                dayNightCalculator = dayNightCalculator,
                currentCalendar = now.toCalendar(),
                units = units,
                iconType = notificationInfo.data.getNotificationIconType()
            ))
        } else {
            UiState.Failure(UnavailableFeature.NETWORK_UNAVAILABLE)
        }
    }
}