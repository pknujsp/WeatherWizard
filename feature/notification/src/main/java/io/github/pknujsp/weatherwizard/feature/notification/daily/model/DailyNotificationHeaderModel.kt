package io.github.pknujsp.weatherwizard.feature.notification.daily.model

import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.data.notification.daily.model.DailyNotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.feature.notification.daily.model.forecast.DailyNotificationForecastUiModel
import io.github.pknujsp.weatherwizard.feature.notification.model.NotificationHeaderModel
import java.time.ZonedDateTime

data class DailyNotificationHeaderModel(
    override val updatedTime: ZonedDateTime, override val state: WeatherResponseState, val notification: DailyNotificationSettingsEntity
) : NotificationHeaderModel() {
    inline fun <reified T : UiModel> map(units: CurrentUnits): T {
        val succeedState = state as WeatherResponseState.Success
        val dayNightCalculator = DayNightCalculator(succeedState.location.latitude, succeedState.location.longitude)

        val uiModel = when (notification.type) {
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