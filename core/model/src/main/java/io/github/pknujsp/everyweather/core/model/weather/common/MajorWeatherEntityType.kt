package io.github.pknujsp.everyweather.core.model.weather.common

import io.github.pknujsp.everyweather.core.model.airquality.AirQualityEntity
import io.github.pknujsp.everyweather.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.everyweather.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.everyweather.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.everyweather.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.everyweather.core.model.weather.yesterday.YesterdayWeatherEntity
import kotlin.reflect.KClass

/**
 * 날씨 엔티티의 대분류를 나타내는 enum class
 * @param entityClass 해당 엔티티의 클래스
 */
enum class MajorWeatherEntityType(val entityClass: KClass<out WeatherEntityModel>) {
    CURRENT_CONDITION(CurrentWeatherEntity::class), HOURLY_FORECAST(HourlyForecastEntity::class),
    DAILY_FORECAST(DailyForecastEntity::class), YESTERDAY_WEATHER(YesterdayWeatherEntity::class), AIR_QUALITY(AirQualityEntity::class);
}