package io.github.pknujsp.weatherwizard.feature.widget.worker.model

import android.widget.RemoteViews
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.domain.weather.ResponseState
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.feature.widget.remoteview.WidgetRemoteViewsCreator
import io.github.pknujsp.weatherwizard.feature.widget.summary.SummaryUiModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class WidgetHeaderUiModel(
    val widgetType: WidgetType, val appWidgetId: Int, val addressName: String, val state: ResponseState, val updatedTime: ZonedDateTime
) : UiModel {

    val updatedTimeText: String = updatedTime.format(dateTimeFormatter)

    private companion object {
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("M.d EEE HH:mm")
    }

    inline fun <reified T : UiModel> map(units: CurrentUnits): T {
        val succeedState = state as ResponseState.Success
        val dayNightCalculator = DayNightCalculator(succeedState.coordinate.latitude, succeedState.coordinate.longitude)

        val uiModel = when (widgetType) {
            WidgetType.SUMMARY -> {
                val currentWeatherEntity = succeedState.entity.toEntity<CurrentWeatherEntity>()
                val hourlyForecastEntity = succeedState.entity.toEntity<HourlyForecastEntity>()
                val dailyForecastEntity = succeedState.entity.toEntity<DailyForecastEntity>()

                SummaryUiModel(currentWeatherEntity, hourlyForecastEntity, dailyForecastEntity, units, dayNightCalculator, updatedTime)
            }

            else -> {
                TODO()
            }
        }

        return uiModel as T
    }
}