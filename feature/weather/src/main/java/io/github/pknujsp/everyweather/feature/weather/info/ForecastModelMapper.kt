package io.github.pknujsp.everyweather.feature.weather.info

import io.github.pknujsp.everyweather.core.common.util.DayNightCalculator
import io.github.pknujsp.everyweather.core.model.settings.CurrentUnits

interface ForecastModelMapper<I, O> {
    fun mapTo(src:I, units: CurrentUnits, dayNightCalculator: DayNightCalculator): O
}