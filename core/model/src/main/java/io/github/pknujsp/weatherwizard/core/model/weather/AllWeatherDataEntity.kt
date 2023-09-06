package io.github.pknujsp.weatherwizard.core.model.weather

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity

data class AllWeatherDataEntity(
    val currentWeatherEntity: CurrentWeatherEntity,
    val hourlyForecastEntity: HourlyForecastEntity,
    val dailyForecastEntity: DailyForecastEntity,
    val yesterdayWeatherEntity: YesterdayWeatherEntity
) : EntityModel