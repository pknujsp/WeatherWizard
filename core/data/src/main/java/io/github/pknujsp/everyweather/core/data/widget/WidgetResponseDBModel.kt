package io.github.pknujsp.everyweather.core.data.widget

import io.github.pknujsp.everyweather.core.model.EntityModel
import io.github.pknujsp.everyweather.core.data.mapper.JsonParser
import io.github.pknujsp.everyweather.core.model.airquality.AirQualityEntity
import io.github.pknujsp.everyweather.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.everyweather.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.everyweather.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.everyweather.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.everyweather.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.everyweather.core.model.weather.yesterday.YesterdayWeatherEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class WidgetResponseDBModel(
    @SerialName("address") val address: String,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("entities") val entities: List<EntityWithWeatherProvider>,
) : EntityModel {

    @Serializable
    class EntityWithWeatherProvider(
        @SerialName("weatherProvider") val weatherProvider: Int,
        @SerialName("entities") val entities: List<Entity>,
    )

    @Serializable
    class Entity(
        @SerialName("type") val type: String,
        @SerialName("data") val data: ByteArray,
    ) {

        companion object {
            private fun realTypeParse(jsonParser: JsonParser, model: Entity) = when (MajorWeatherEntityType.valueOf(model.type)) {
                MajorWeatherEntityType.CURRENT_CONDITION -> jsonParser.parse<CurrentWeatherEntity>(model.data)
                MajorWeatherEntityType.YESTERDAY_WEATHER -> jsonParser.parse<YesterdayWeatherEntity>(model.data)
                MajorWeatherEntityType.HOURLY_FORECAST -> jsonParser.parse<HourlyForecastEntity>(model.data)
                MajorWeatherEntityType.DAILY_FORECAST -> jsonParser.parse<DailyForecastEntity>(model.data)
                MajorWeatherEntityType.AIR_QUALITY -> jsonParser.parse<AirQualityEntity>(model.data)
            }
        }

        fun toWeatherEntity(jsonParser: JsonParser): WeatherEntityModel {
            return realTypeParse(jsonParser, this)
        }
    }

    fun toByteArray(jsonParser: JsonParser): ByteArray {
        return jsonParser.parseToByteArray(this)
    }

}