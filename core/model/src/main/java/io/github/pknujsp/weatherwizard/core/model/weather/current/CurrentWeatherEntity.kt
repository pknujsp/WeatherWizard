package io.github.pknujsp.weatherwizard.core.model.weather.current

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CloudinessType
import io.github.pknujsp.weatherwizard.core.model.weather.common.DewPointType
import io.github.pknujsp.weatherwizard.core.model.weather.common.HumidityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PressureType
import io.github.pknujsp.weatherwizard.core.model.weather.common.UVIndexType
import io.github.pknujsp.weatherwizard.core.model.weather.common.VisibilityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionType
import io.github.pknujsp.weatherwizard.core.model.weather.common.item.PrecipitationEntity
import io.github.pknujsp.weatherwizard.core.model.weather.common.item.TemperatureEntity
import io.github.pknujsp.weatherwizard.core.model.weather.common.item.WindEntity

data class CurrentWeatherEntity(
    val weatherCondition: WeatherConditionType,
    val temperature: TemperatureEntity,
    val dewPoint: DewPointType,
    val humidity: HumidityType,
    val windEntity: WindEntity,
    val pressure: PressureType,
    val uvIndex: UVIndexType,
    val visibility: VisibilityType,
    val cloudiness: CloudinessType,
    val precipitation: PrecipitationEntity,
) : EntityModel