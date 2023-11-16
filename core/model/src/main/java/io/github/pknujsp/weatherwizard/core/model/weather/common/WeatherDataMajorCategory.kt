package io.github.pknujsp.weatherwizard.core.model.weather.common

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.airquality.AirQualityEntity
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity
import kotlin.reflect.KClass

enum class WeatherDataMajorCategory(val entityClass: KClass<out EntityModel>) {
    CURRENT_CONDITION(CurrentWeatherEntity::class), HOURLY_FORECAST(HourlyForecastEntity::class),
    DAILY_FORECAST(DailyForecastEntity::class), AIR_QUALITY(AirQualityEntity::class),
    YESTERDAY_WEATHER(YesterdayWeatherEntity::class);
}