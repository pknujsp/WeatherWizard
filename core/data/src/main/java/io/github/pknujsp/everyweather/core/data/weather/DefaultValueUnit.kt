package io.github.pknujsp.everyweather.core.data.weather

import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationUnit
import io.github.pknujsp.everyweather.core.model.weather.common.PressureUnit
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.everyweather.core.model.weather.common.VisibilityUnit
import io.github.pknujsp.everyweather.core.model.weather.common.WindDirectionUnit
import io.github.pknujsp.everyweather.core.model.weather.common.WindSpeedUnit

interface DefaultValueUnit {
    val DEFAULT_TEMPERATURE_UNIT : TemperatureUnit
    val DEFAULT_WIND_SPEED_UNIT : WindSpeedUnit
    val DEFAULT_WIND_DIRECTION_UNIT : WindDirectionUnit
    val DEFAULT_PRECIPITATION_UNIT : PrecipitationUnit
    val DEFAULT_VISIBILITY_UNIT : VisibilityUnit
    val DEFAULT_PRESSURE_UNIT : PressureUnit
}