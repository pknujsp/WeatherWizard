package io.github.pknujsp.weatherwizard.core.model.weather.common

data class CurrentUnits(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.default,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.default,
    val precipitationUnit: PrecipitationUnit = PrecipitationUnit.default
)