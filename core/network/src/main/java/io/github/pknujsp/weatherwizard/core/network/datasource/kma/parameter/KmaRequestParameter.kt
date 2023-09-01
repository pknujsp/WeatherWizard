package io.github.pknujsp.weatherwizard.core.network.datasource.kma.parameter

import io.github.pknujsp.weatherwizard.core.network.ApiRequestParameter


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