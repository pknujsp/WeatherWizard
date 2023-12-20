package io.github.pknujsp.weatherwizard.core.widgetnotification.widget.worker.model

import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetSettingsEntity
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.summary.WidgetAllInOneRemoteViewUiModel
import java.time.ZonedDateTime

class WidgetHeaderUiModel(
    val widget: WidgetSettingsEntity, val state: WeatherResponseState, val updatedTime: ZonedDateTime
) : UiModel {

    inline fun <reified T : UiModel> map(units: CurrentUnits): T {
        val succeedState = state as WeatherResponseState.Success
        val dayNightCalculator = DayNightCalculator(succeedState.location.latitude, succeedState.location.longitude)

        val uiModel = when (widget.widgetType) {
            WidgetType.ALL_IN_ONE -> {
                val currentWeatherEntity = succeedState.entity.toEntity<CurrentWeatherEntity>()
                val hourlyForecastEntity = succeedState.entity.toEntity<HourlyForecastEntity>()
                val dailyForecastEntity = succeedState.entity.toEntity<DailyForecastEntity>()

                WidgetAllInOneRemoteViewUiModel(currentWeatherEntity, hourlyForecastEntity, dailyForecastEntity, units, dayNightCalculator, updatedTime)
            }

            else -> {
                TODO()
            }
        }

        return uiModel as T
    }
}