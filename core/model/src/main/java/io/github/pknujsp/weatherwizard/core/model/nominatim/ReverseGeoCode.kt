package io.github.pknujsp.weatherwizard.core.model.nominatim

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import java.time.ZonedDateTime

data class ReverseGeoCode(
    val displayName: String,
    val countryCode: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val requestDateTime: String,
) : UiModel