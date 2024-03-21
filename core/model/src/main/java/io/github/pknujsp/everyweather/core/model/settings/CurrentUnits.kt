package io.github.pknujsp.everyweather.core.model.settings

import io.github.pknujsp.everyweather.core.model.Model
import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.everyweather.core.model.weather.common.WindSpeedUnit

data class CurrentUnits(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.default,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.default,
    val precipitationUnit: PrecipitationUnit = PrecipitationUnit.default,
) : Model
