package io.github.pknujsp.weatherwizard.core.model.weather.common

import kotlinx.serialization.Serializable

interface WeatherDataUnit {
    val symbol: String
}

interface UnitPreference {
    val key: String
    val default: WeatherDataUnit
    val units: Array<out WeatherDataUnit>

    fun getUnit(symbol: String): WeatherDataUnit

}

@Serializable
sealed class TemperatureUnit(override val symbol: String) : WeatherDataUnit {

    companion object : UnitPreference {
        override val key: String = "temperature_unit"
        override val default get() = Celsius
        override val units: Array<TemperatureUnit> get() = arrayOf(Celsius, Fahrenheit)

        override fun getUnit(symbol: String): TemperatureUnit {
            return when (symbol) {
                Celsius.symbol -> Celsius
                Fahrenheit.symbol -> Fahrenheit
                else -> Celsius
            }
        }
    }

    @Serializable
    data object Celsius : TemperatureUnit("℃")

    @Serializable
    data object Fahrenheit : TemperatureUnit("℉")

}

@Serializable
sealed class WindSpeedUnit(override val symbol: String) : WeatherDataUnit {

    companion object : UnitPreference {
        override val key: String = "wind_speed_unit"
        override val default get() = MeterPerSecond
        override val units: Array<WindSpeedUnit> get() = arrayOf(KilometerPerHour, MeterPerSecond)

        override fun getUnit(symbol: String): WindSpeedUnit {
            return when (symbol) {
                KilometerPerHour.symbol -> KilometerPerHour
                MeterPerSecond.symbol -> MeterPerSecond
                else -> KilometerPerHour
            }
        }
    }

    @Serializable
    data object KilometerPerHour : WindSpeedUnit("km/h")

    @Serializable
    data object MeterPerSecond : WindSpeedUnit("m/s")


}

@Serializable
sealed class PrecipitationUnit(override val symbol: String) : WeatherDataUnit {

    companion object : UnitPreference {
        override val key: String = "precipitation_unit"
        override val default get() = Millimeter
        override val units: Array<PrecipitationUnit> get() = arrayOf(Millimeter, Centimeter)
        override fun getUnit(symbol: String): PrecipitationUnit {
            return when (symbol) {
                Millimeter.symbol -> Millimeter
                Centimeter.symbol -> Centimeter
                else -> Millimeter
            }
        }
    }

    @Serializable
    data object Millimeter : PrecipitationUnit("mm")

    @Serializable
    data object Centimeter : PrecipitationUnit("cm")
}

@Serializable
sealed class VisibilityUnit(override val symbol: String) : WeatherDataUnit {
    @Serializable
    data object Kilometer : VisibilityUnit("km")

}

@Serializable
sealed class PressureUnit(override val symbol: String) : WeatherDataUnit {
    @Serializable
    data object Hectopascal : PressureUnit("hPa")
}

@Serializable
sealed class WindDirectionUnit(override val symbol: String) : WeatherDataUnit {
    @Serializable
    data object Degree : WindDirectionUnit("°")
}

@Serializable
data object PercentageUnit : WeatherDataUnit {
    @Serializable override val symbol: String = "%"
}