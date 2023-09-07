package io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter

import io.github.pknujsp.weatherwizard.core.model.ApiRequestParameter


data class KmaCurrentWeatherRequestParameter(
    val code: String,
    override val requestId: Long
) : ApiRequestParameter


data class KmaHourlyForecastRequestParameter(
    val code: String,
    override val requestId: Long
) : ApiRequestParameter


data class KmaDailyForecastRequestParameter(
    val code: String,
    override val requestId: Long
) : ApiRequestParameter

data class KmaYesterdayWeatherRequestParameter(
    val code: String,
    override val requestId: Long
) : ApiRequestParameter