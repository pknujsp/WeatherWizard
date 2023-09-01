package io.github.pknujsp.weatherwizard.core.model.weather.common


abstract class WeatherDataUnit(open val symbol: String) {
    abstract fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType
}


sealed class TemperatureUnit(override val symbol: String) : WeatherDataUnit(symbol) {
    object Celsius : TemperatureUnit("℃") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType =
            if (to == Fahrenheit) value * (9.0 / 5.0) + 32.0 else value

    }

    object Fahrenheit : TemperatureUnit("℉") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType =
            if (to == Celsius) (value - 32.0) * (5.0 / 9.0) else value

    }

}


sealed class WindSpeedUnit(override val symbol: String) : WeatherDataUnit(symbol) {
    object KilometerPerHour : WindSpeedUnit("km/h") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = when (to) {
            MeterPerSecond -> value / 3.6
            MilesPerHour -> value / 1.609344
            Knot -> value / 1.852
            else -> value
        }
    }

    object MeterPerSecond : WindSpeedUnit("m/s") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = when (to) {
            KilometerPerHour -> value * 3.6
            MilesPerHour -> value * 2.236936
            Knot -> value * 1.943844
            else -> value
        }
    }

    object MilesPerHour : WindSpeedUnit("mph") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = when (to) {
            KilometerPerHour -> value * 1.609344
            MeterPerSecond -> value / 2.236936
            Knot -> value / 1.150779
            else -> value
        }
    }

    object Knot : WindSpeedUnit("knot") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = when (to) {
            KilometerPerHour -> value * 1.852
            MeterPerSecond -> value / 1.943844
            MilesPerHour -> value * 1.150779
            else -> value
        }
    }

}

sealed class PrecipitationUnit(override val symbol: String) : WeatherDataUnit(symbol) {
    object Millimeter : PrecipitationUnit("mm") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = when (to) {
            Inch -> value / 25.4
            Centimeter -> value / 10
            else -> value
        }
    }

    object Inch : PrecipitationUnit("in") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = when (to) {
            Millimeter -> value * 25.4
            Centimeter -> value * 2.54
            else -> value
        }
    }

    object Centimeter : PrecipitationUnit("cm") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = when (to) {
            Millimeter -> value * 10
            Inch -> value / 2.54
            else -> value
        }
    }
}


sealed class VisibilityUnit(override val symbol: String) : WeatherDataUnit(symbol) {
    object Kilometer : VisibilityUnit("km") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = when (to) {
            Mile -> value / 1.609344
            else -> value
        }
    }

    object Mile : VisibilityUnit("mi") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = when (to) {
            Kilometer -> value * 1.609344
            else -> value
        }
    }
}

sealed class PressureUnit(override val symbol: String) : WeatherDataUnit(symbol) {
    object Hectopascal : PressureUnit("hPa") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = when (to) {
            InchOfMercury -> value / 33.863886666667
            Millibar -> value
            else -> value
        }
    }

    object InchOfMercury : PressureUnit("inHg") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = when (to) {
            Hectopascal -> value * 33.863886666667
            Millibar -> value * 33.863886666667
            else -> value
        }
    }

    object Millibar : PressureUnit("mb") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = when (to) {
            Hectopascal -> value
            InchOfMercury -> value / 33.863886666667
            else -> value
        }
    }
}


sealed class WindDirectionUnit(override val symbol: String) : WeatherDataUnit(symbol) {
    object Degree : WindDirectionUnit("°") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = when (to) {
            Compass16 -> value / 22.5
            Compass8 -> value / 45
            Compass4 -> value / 90
            else -> value
        }
    }

    object Compass16 : WindDirectionUnit("16") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = when (to) {
            Degree -> value * 22.5
            Compass8 -> value / 2
            Compass4 -> value / 4
            else -> value
        }
    }

    object Compass8 : WindDirectionUnit("8") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = when (to) {
            Degree -> value * 45
            Compass16 -> value * 2
            Compass4 -> value / 2
            else -> value
        }
    }

    object Compass4 : WindDirectionUnit("4") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = when (to) {
            Degree -> value * 90
            Compass16 -> value * 4
            Compass8 -> value * 2
            else -> value
        }
    }
}


sealed class AirQualityUnit(override val symbol: String) : WeatherDataUnit(symbol) {
    object AQI : AirQualityUnit("AQI") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = value
    }
}

sealed class UVIndexUnit(override val symbol: String) : WeatherDataUnit(symbol) {
    object Index : UVIndexUnit("index") {
        override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = value
    }
}

sealed class PercentUnit(override val symbol: String = "%") : WeatherDataUnit(symbol) {
    override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit): WeatherDoubleValueType = value
}