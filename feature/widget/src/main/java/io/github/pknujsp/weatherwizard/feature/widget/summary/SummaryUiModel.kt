package io.github.pknujsp.weatherwizard.feature.widget.summary

import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity

class SummaryUiModel(
    currentWeatherEntity: CurrentWeatherEntity,
    hourlyForecastEntity: HourlyForecastEntity,
    dailyForecastEntity: DailyForecastEntity,
    units: CurrentUnits,
    dayNightCalculator: DayNightCalculator
) : UiModel