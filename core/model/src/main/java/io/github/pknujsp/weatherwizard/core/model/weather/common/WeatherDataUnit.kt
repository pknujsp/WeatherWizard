package io.github.pknujsp.weatherwizard.core.model.weather.common


interface WeatherDataUnit {
    val symbol: String
}

interface UnitPreference {
    val key: String
    val default: WeatherDataUnit
    val units: Array<out WeatherDataUnit>

    fun getUnit(symbol: String): WeatherDataUnit

}

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

    data object Celsius : TemperatureUnit("℃")

    data object Fahrenheit : TemperatureUnit("℉")

}


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

    data object KilometerPerHour : WindSpeedUnit("km/h")

    data object MeterPerSecond : WindSpeedUnit("m/s")


}

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

data object PercentageUnit : WeatherDataUnit {

    override val symbol: String = "%"
}