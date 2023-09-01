package io.github.pknujsp.weatherwizard.core.model.weather.airquality

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.Coordinate

data class StationEntity(
    val stationName: String,
    val coordinate: Coordinate
) : EntityModel