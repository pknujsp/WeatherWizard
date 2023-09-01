package io.github.pknujsp.weatherwizard.core.model.weather.common.item

import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionDegreeType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedType

data class WindEntity(
    val speed: WindSpeedType,
    val direction: WindDirectionType,
    val directionDegree: WindDirectionDegreeType,
    val gust: WindSpeedType = WindSpeedType.emptyValue(),
) {
    companion object {
        fun emptyEntity(): WindEntity {
            return WindEntity(
                WindSpeedType.emptyValue(),
                WindDirectionType.emptyValue(),
                WindDirectionDegreeType.emptyValue(),
                WindSpeedType.emptyValue()
            )
        }
    }
}