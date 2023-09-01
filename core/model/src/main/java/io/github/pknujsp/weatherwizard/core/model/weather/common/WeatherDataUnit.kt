package io.github.pknujsp.weatherwizard.core.model.weather.common


abstract class WeatherDataUnit<T : WeatherDataType>(open val symbol: String) {
    abstract fun convert(value: T, to: WeatherDataUnit<*>): T
}


sealed class TemperatureUnit(override val symbol: String) : WeatherDataUnit<TemperatureType>(symbol) {
    object Celsius : TemperatureUnit("℃") {
        override fun convert(value: TemperatureType, to: WeatherDataUnit<*>): TemperatureType =
            if (to == Fahrenheit) (value.value * (9.0 / 5.0) + 32.0).toTemperatureType() else value

    }

    object Fahrenheit : TemperatureUnit("℉") {
        override fun convert(value: TemperatureType, to: WeatherDataUnit<*>): TemperatureType =
            if (to == Celsius) ((value.value - 32.0) * (5.0 / 9.0)).toTemperatureType() else value

    }

}


sealed class WindSpeedUnit(override val symbol: String) : WeatherDataUnit<WindSpeedType>(symbol) {
    object KilometerPerHour : WindSpeedUnit("km/h") {
        override fun convert(value: WindSpeedType, to: WeatherDataUnit<*>): WindSpeedType = when (to) {
            MeterPerSecond -> (value.value / 3.6).toWindSpeedType()
            MilesPerHour -> (value.value / 1.609344).toWindSpeedType()
            Knot -> (value.value / 1.852).toWindSpeedType()
            else -> value
        }
    }

    object MeterPerSecond : WindSpeedUnit("m/s") {
        override fun convert(value: WindSpeedType, to: WeatherDataUnit<*>): WindSpeedType = when (to) {
            KilometerPerHour -> (value.value * 3.6).toWindSpeedType()
            MilesPerHour -> (value.value * 2.236936).toWindSpeedType()
            Knot -> (value.value * 1.943844).toWindSpeedType()
            else -> value
        }
    }

    object MilesPerHour : WindSpeedUnit("mph") {
        override fun convert(value: WindSpeedType, to: WeatherDataUnit<*>): WindSpeedType = when (to) {
            KilometerPerHour -> (value.value * 1.609344).toWindSpeedType()
            MeterPerSecond -> (value.value / 2.236936).toWindSpeedType()
            Knot -> (value.value / 1.150779).toWindSpeedType()
            else -> value
        }
    }

    object Knot : WindSpeedUnit("knot") {
        override fun convert(value: WindSpeedType, to: WeatherDataUnit<*>): WindSpeedType = when (to) {
            KilometerPerHour -> (value.value * 1.852).toWindSpeedType()
            MeterPerSecond -> (value.value / 1.943844).toWindSpeedType()
            MilesPerHour -> (value.value * 1.150779).toWindSpeedType()
            else -> value
        }
    }

}

sealed class PrecipitationUnit(override val symbol: String) : WeatherDataUnit<PrecipitationVolumeType>(symbol) {
    object Millimeter : PrecipitationUnit("mm") {
        override fun convert(value: PrecipitationVolumeType, to: WeatherDataUnit<*>): PrecipitationVolumeType = when (to) {
            Inch -> (value.value / 25.4).toPrecipitationVolumeType()
            Centimeter -> (value.value / 10).toPrecipitationVolumeType()
            else -> value
        }
    }

    object Inch : PrecipitationUnit("in") {
        override fun convert(value: PrecipitationVolumeType, to: WeatherDataUnit<*>): PrecipitationVolumeType = when (to) {
            Millimeter -> (value.value * 25.4).toPrecipitationVolumeType()
            Centimeter -> (value.value * 2.54).toPrecipitationVolumeType()
            else -> value
        }
    }

    object Centimeter : PrecipitationUnit("cm") {
        override fun convert(value: PrecipitationVolumeType, to: WeatherDataUnit<*>): PrecipitationVolumeType = when (to) {
            Millimeter -> (value.value * 10).toPrecipitationVolumeType()
            Inch -> (value.value / 2.54).toPrecipitationVolumeType()
            else -> value
        }
    }
}


sealed class VisibilityUnit(override val symbol: String) : WeatherDataUnit<VisibilityType>(symbol) {
    object Kilometer : VisibilityUnit("km") {
        override fun convert(value: VisibilityType, to: WeatherDataUnit<*>): VisibilityType = when (to) {
            Mile -> (value.value / 1.609344).toVisibilityType()
            else -> value
        }
    }

    object Mile : VisibilityUnit("mi") {
        override fun convert(value: VisibilityType, to: WeatherDataUnit<*>): VisibilityType = when (to) {
            Kilometer -> (value.value * 1.609344).toVisibilityType()
            else -> value
        }
    }
}

sealed class PressureUnit(override val symbol: String) : WeatherDataUnit<PressureType>(symbol) {
    object Hectopascal : PressureUnit("hPa") {
        override fun convert(value: PressureType, to: WeatherDataUnit<*>): PressureType = when (to) {
            InchOfMercury -> (value.value / 33.863886666667).toPressureType()
            Millibar -> value
            else -> value
        }
    }

    object InchOfMercury : PressureUnit("inHg") {
        override fun convert(value: PressureType, to: WeatherDataUnit<*>): PressureType = when (to) {
            Hectopascal -> (value.value * 33.863886666667).toPressureType()
            Millibar -> (value.value * 33.863886666667).toPressureType()
            else -> value
        }
    }

    object Millibar : PressureUnit("mb") {
        override fun convert(value: PressureType, to: WeatherDataUnit<*>): PressureType = when (to) {
            Hectopascal -> (value.value).toPressureType()
            InchOfMercury -> (value.value / 33.863886666667).toPressureType()
            else -> value
        }
    }
}


sealed class WindDirectionUnit(override val symbol: String) : WeatherDataUnit<WindDirectionType>(symbol) {
    object Degree : WindDirectionUnit("°") {
        override fun convert(value: WindDirectionType, to: WeatherDataUnit<*>): WindDirectionType = when (to) {
            Compass16 -> (value.value / 22.5).toWindDirectionType()
            Compass8 -> (value.value / 45).toWindDirectionType()
            Compass4 -> (value.value / 90).toWindDirectionType()
            else -> value
        }
    }

    object Compass16 : WindDirectionUnit("16") {
        override fun convert(value: WindDirectionType, to: WeatherDataUnit<*>): WindDirectionType = when (to) {
            Degree -> (value.value * 22.5).toWindDirectionType()
            Compass8 -> (value.value / 2).toWindDirectionType()
            Compass4 -> (value.value / 4).toWindDirectionType()
            else -> value
        }
    }

    object Compass8 : WindDirectionUnit("8") {
        override fun convert(value: WindDirectionType, to: WeatherDataUnit<*>): WindDirectionType = when (to) {
            Degree -> (value.value * 45).toWindDirectionType()
            Compass16 -> (value.value * 2).toWindDirectionType()
            Compass4 -> (value.value / 2).toWindDirectionType()
            else -> value
        }
    }

    object Compass4 : WindDirectionUnit("4") {
        override fun convert(value: WindDirectionType, to: WeatherDataUnit<*>): WindDirectionType = when (to) {
            Degree -> (value.value * 90).toWindDirectionType()
            Compass16 -> (value.value * 4).toWindDirectionType()
            Compass8 -> (value.value * 2).toWindDirectionType()
            else -> value
        }
    }
}


sealed class AirQualityUnit(override val symbol: String) : WeatherDataUnit<AirQualityType>(symbol) {
    object AQI : AirQualityUnit("AQI") {
        override fun convert(value: AirQualityType, to: WeatherDataUnit<*>): AirQualityType = value
    }
}

sealed class UVIndexUnit(override val symbol: String) : WeatherDataUnit<UVIndexType>(symbol) {
    object Index : UVIndexUnit("index") {
        override fun convert(value: UVIndexType, to: WeatherDataUnit<*>): UVIndexType = value
    }
}

sealed class PercentUnit(override val symbol: String = "%") : WeatherDataUnit<WeatherDoubleValueType>(symbol) {
    override fun convert(value: WeatherDoubleValueType, to: WeatherDataUnit<*>): WeatherDoubleValueType = value
}