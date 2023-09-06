package io.github.pknujsp.weatherwizard.core.model.weather.common


interface WeatherDataUnit {
    val symbol: String
}


sealed class TemperatureUnit(override val symbol: String) : WeatherDataUnit {

    data object Celsius : TemperatureUnit("℃")

    data object Fahrenheit : TemperatureUnit("℉")

}


sealed class WindSpeedUnit(override val symbol: String) : WeatherDataUnit {
    data object KilometerPerHour : WindSpeedUnit("km/h")

    data object MeterPerSecond : WindSpeedUnit("m/s")


}

sealed class PrecipitationUnit(override val symbol: String) : WeatherDataUnit {
    data object Millimeter : PrecipitationUnit("mm")

    data object Centimeter : PrecipitationUnit("cm")
}


sealed class VisibilityUnit(override val symbol: String) : WeatherDataUnit {
    data object Kilometer : VisibilityUnit("km")

}

sealed class PressureUnit(override val symbol: String) : WeatherDataUnit {
    data object Hectopascal : PressureUnit("hPa")
}


sealed class WindDirectionUnit(override val symbol: String) : WeatherDataUnit {
    data object Degree : WindDirectionUnit("°")

}

sealed class PercentageUnit(override val symbol: String) : WeatherDataUnit {
    data object Percent : PercentageUnit("%")
}