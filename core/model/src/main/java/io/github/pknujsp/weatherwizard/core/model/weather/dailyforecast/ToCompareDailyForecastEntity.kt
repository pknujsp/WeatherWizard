package io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

data class ToCompareDailyForecastEntity(
    val items: List<Pair<WeatherProvider, DailyForecastEntity>>
) : EntityModel