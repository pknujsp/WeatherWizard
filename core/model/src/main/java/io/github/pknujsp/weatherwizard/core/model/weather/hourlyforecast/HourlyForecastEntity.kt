package io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CloudinessType
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeType
import io.github.pknujsp.weatherwizard.core.model.weather.common.DewPointType
import io.github.pknujsp.weatherwizard.core.model.weather.common.HumidityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PressureType
import io.github.pknujsp.weatherwizard.core.model.weather.common.UVIndexType
import io.github.pknujsp.weatherwizard.core.model.weather.common.VisibilityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionType
import io.github.pknujsp.weatherwizard.core.model.weather.common.item.PrecipitationEntity
import io.github.pknujsp.weatherwizard.core.model.weather.common.item.TemperatureEntity
import io.github.pknujsp.weatherwizard.core.model.weather.common.item.WindEntity

data class HourlyForecastEntity(
    val items: List<Item>
) : EntityModel {
    data class Item(
        val dateTime: DateTimeType,
        val weatherCondition: WeatherConditionType,
        val temperature: TemperatureEntity,
        val dewPoint: DewPointType = DewPointType.emptyValue(),
        val humidity: HumidityType,
        val wind: WindEntity,
        val pressure: PressureType = PressureType.emptyValue(),
        val uvIndex: UVIndexType = UVIndexType.emptyValue(),
        val visibility: VisibilityType = VisibilityType.emptyValue(),
        val cloudiness: CloudinessType = CloudinessType.emptyValue(),
        val precipitation: PrecipitationEntity,
        val thunder: Boolean
    )
}