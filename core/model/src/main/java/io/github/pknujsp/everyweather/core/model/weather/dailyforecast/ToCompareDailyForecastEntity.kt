package io.github.pknujsp.everyweather.core.model.weather.dailyforecast

import io.github.pknujsp.everyweather.core.model.EntityModel
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider

data class ToCompareDailyForecastEntity(
    val items: List<Pair<WeatherProvider, DailyForecastEntity>>
) : EntityModel