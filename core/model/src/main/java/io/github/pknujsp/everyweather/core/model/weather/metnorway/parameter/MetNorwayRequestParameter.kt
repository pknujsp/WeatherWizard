package io.github.pknujsp.everyweather.core.model.weather.metnorway.parameter

import io.github.pknujsp.everyweather.core.model.ApiRequestParameter


data class MetNorwayRequestParameter(
    val latitude: Double,
    val longitude: Double,
    override val requestId: Long
) : ApiRequestParameter