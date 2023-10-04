package io.github.pknujsp.weatherwizard.core.model.weather.metnorway.parameter

import io.github.pknujsp.weatherwizard.core.model.ApiRequestParameter


data class MetNorwayRequestParameter(
    val latitude: Double,
    val longitude: Double,
    override val requestId: Long
) : ApiRequestParameter