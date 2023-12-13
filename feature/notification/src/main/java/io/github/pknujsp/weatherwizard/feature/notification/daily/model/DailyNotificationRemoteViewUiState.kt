package io.github.pknujsp.weatherwizard.feature.notification.daily.model

import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.feature.notification.daily.model.forecast.DailyNotificationForecastUiModel
import io.github.pknujsp.weatherwizard.core.model.remoteviews.RemoteViewUiState

data class DailyNotificationRemoteViewUiState<T : RemoteViewUiModel>(
    val type: DailyNotificationType = DailyNotificationType.default, override val model: T
) : RemoteViewUiState<T> {
    inline fun <reified T : UiModel> map(units: CurrentUnits): T {
        val succeedState = state as WeatherResponseState.Success
        val dayNightCalculator = DayNightCalculator(succeedState.location.latitude, succeedState.location.longitude)

        val uiModel = when (settings.type) {
            DailyNotificationType.FORECAST -> {
                val hourlyForecastEntity = succeedState.entity.toEntity<HourlyForecastEntity>()
                val dailyForecastEntity = succeedState.entity.toEntity<DailyForecastEntity>()

                DailyNotificationForecastUiModel(notification.location.address,
                    hourlyForecastEntity,
                    dailyForecastEntity,
                    dayNightCalculator,
                    units)
            }

            else -> {
                TODO()
            }
        }

        return uiModel as T
    }

}