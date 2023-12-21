package io.github.pknujsp.weatherwizard.core.widgetnotification.widget.worker.model

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class WidgetResponseDBModel(
    @SerialName("address") val address: String,
    @SerialName("entities") val entities: List<Entity>,
) : EntityModel {

    @Serializable
    class Entity(
        @SerialName("type") val type: String,
        @SerialName("data") val data: ByteArray,
    ) {
        fun toMajorWeatherEntityType() = MajorWeatherEntityType.valueOf(type)
        fun toEntityModel(jsonParser: JsonParser): WeatherEntityModel {
            return jsonParser.parse(data)
        }
    }

    fun toByteArray(jsonParser: JsonParser): ByteArray {
        return jsonParser.parseToByteArray(this)
    }
}