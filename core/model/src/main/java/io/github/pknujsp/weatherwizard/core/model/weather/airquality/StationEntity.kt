package io.github.pknujsp.weatherwizard.core.model.weather.airquality

import io.github.pknujsp.weatherwizard.core.model.EntityModel

data class StationEntity(
    val stationName: String,
    val coordinate: Pair<Double, Double>,
) : EntityModel