package io.github.pknujsp.weatherwizard.core.model.weather.common.item

import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedType

data class WindEntity(
    val speed: WindSpeedType,
    val direction: WindDirectionType,
    val gust: WindSpeedType = WindSpeedType.emptyValue(),
) {
    companion object {
        fun emptyEntity(): WindEntity {
            return WindEntity(
                WindSpeedType.emptyValue(),
                WindDirectionType.emptyValue(),
                WindSpeedType.emptyValue()
            )
        }
    }
}