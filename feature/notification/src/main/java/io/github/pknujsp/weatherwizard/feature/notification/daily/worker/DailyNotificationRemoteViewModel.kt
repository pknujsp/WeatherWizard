package io.github.pknujsp.weatherwizard.feature.notification.daily.worker

import io.github.pknujsp.weatherwizard.core.common.UnavailableFeature
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.notification.NotificationRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.domain.weather.GetCurrentWeatherUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetDailyForecastUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetHourlyForecastUseCase
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationEntity
import io.github.pknujsp.weatherwizard.core.model.notification.daily.DailyNotificationInfo
import io.github.pknujsp.weatherwizard.core.model.notification.daily.DailyNotificationInfoEntity
import io.github.pknujsp.weatherwizard.core.model.notification.daily.hourlyforecast.DailyNotificationHourlyForecastUiModel
import io.github.pknujsp.weatherwizard.core.model.notification.ongoing.OngoingNotificationUiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.feature.notification.common.RemoteViewModel
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.properties.Delegates

class DailyNotificationRemoteViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getHourlyForecastUseCase: GetHourlyForecastUseCase,
    private val getDailyForecastUseCase: GetDailyForecastUseCase,
    private val appSettingsRepository: SettingsRepository,
    private val notificationRepository: NotificationRepository,
    private val nominatimRepository: NominatimRepository,
) : RemoteViewModel() {

    private var units: CurrentUnits by Delegates.notNull()
    var notificationInfo: NotificationEntity<DailyNotificationInfoEntity> by Delegates.notNull()
        private set

    private val now = ZonedDateTime.now()
    private val requestId = now.toInstant().toEpochMilli()

    private var address: String by Delegates.notNull()

    suspend fun init(notificationId: Long) {
        appSettingsRepository.init()
        units = appSettingsRepository.currentUnits.value
        notificationInfo = notificationRepository.getDailyNotification(notificationId)
        notificationInfo.data.run {
            address = if (getLocationType() is LocationType.CurrentLocation) nominatimRepository.reverseGeoCode(
                latitude, longitude).getOrThrow().simpleDisplayName
            else addressName
        }

    }

    suspend fun loadHourlyForecast(): UiState<DailyNotificationHourlyForecastUiModel> {
        val latitude = notificationInfo.data.latitude
        val longitude = notificationInfo.data.longitude

        val weatherProvider = notificationInfo.data.getWeatherProvider()
        val hourlyForecast =
            getHourlyForecastUseCase(latitude, longitude, weatherProvider, requestId).getOrNull()

        return if (hourlyForecast != null) {
            val dayNightCalculator = DayNightCalculator(latitude, longitude)

            UiState.Success(DailyNotificationHourlyForecastUiModel(
                address,
                hourlyForecast,
                dayNightCalculator,
                units,
            ))
        } else {
            UiState.Failure(UnavailableFeature.NETWORK_UNAVAILABLE)
        }
    }
}