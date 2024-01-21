package io.github.pknujsp.weatherwizard.core.model.weather.common

import io.github.pknujsp.weatherwizard.core.model.settings.BasePreferenceModel
import io.github.pknujsp.weatherwizard.core.model.settings.PreferenceModel
import kotlinx.serialization.Serializable

interface WeatherDataUnit : PreferenceModel {
    val symbol: String
}

interface UnitPreference<T : WeatherDataUnit> : BasePreferenceModel<T> {

}

@Serializable
sealed class TemperatureUnit(override val symbol: String) : WeatherDataUnit {

    companion object : UnitPreference<TemperatureUnit> {
        override val key: String = "temperature_unit"
        override val default get() = Celsius
        override val enums: Array<TemperatureUnit> get() = arrayOf(Celsius, Fahrenheit)
    }

    @Serializable
    data object Celsius : TemperatureUnit("℃") {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.celsius
        override val key: Int = 0
        override val icon: Int? = null
    }

    @Serializable
    data object Fahrenheit : TemperatureUnit("℉") {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.fahrenheit
        override val icon: Int? = null
        override val key: Int = 1
    }

}

@Serializable
sealed class WindSpeedUnit(override val symbol: String) : WeatherDataUnit {

    companion object : UnitPreference<WindSpeedUnit> {
        override val key: String = "wind_speed_unit"
        override val default get() = MeterPerSecond
        override val enums: Array<WindSpeedUnit> get() = arrayOf(KilometerPerHour, MeterPerSecond)
    }

    @Serializable
    data object KilometerPerHour : WindSpeedUnit("km/h") {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.kilometer_per_hour
        override val icon: Int? = null
        override val key: Int = 0
    }

    @Serializable
    data object MeterPerSecond : WindSpeedUnit("m/s") {
        override val icon: Int? = null
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.meter_per_second
        override val key: Int = 1
    }


}

@Serializable
sealed class PrecipitationUnit(override val symbol: String) : WeatherDataUnit {

    companion object : UnitPreference<PrecipitationUnit> {
        override val key: String = "precipitation_unit"
        override val default get() = Millimeter
        override val enums: Array<PrecipitationUnit> get() = arrayOf(Millimeter, Centimeter)
    }

    @Serializable
    data object Millimeter : PrecipitationUnit("mm") {
        override val icon: Int? = null
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.millimeter
        override val key: Int = 0
    }

    @Serializable
    data object Centimeter : PrecipitationUnit("cm") {
        override val icon: Int? = null
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.centimeter
        override val key: Int = 1
    }
}

@Serializable
sealed class VisibilityUnit(override val symbol: String) : WeatherDataUnit {
    companion object : UnitPreference<VisibilityUnit> {
        override val key: String = "visibility_unit"
        override val default get() = Kilometer
        override val enums: Array<VisibilityUnit> get() = arrayOf(Kilometer)
    }

    @Serializable
    data object Kilometer : VisibilityUnit("km") {
        override val icon: Int? = null
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.kilometer
        override val key: Int = 0
    }
}

@Serializable
sealed class PressureUnit(override val symbol: String) : WeatherDataUnit {
    companion object : UnitPreference<PressureUnit> {
        override val key: String = "pressure_unit"
        override val default get() = Hectopascal
        override val enums: Array<PressureUnit> get() = arrayOf(Hectopascal)
    }

    @Serializable
    data object Hectopascal : PressureUnit("hPa") {
        override val icon: Int? = null
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.hectopascal
        override val key: Int = 0
    }
}

@Serializable
sealed class WindDirectionUnit(override val symbol: String) : WeatherDataUnit {
    companion object : UnitPreference<WindDirectionUnit> {
        override val key: String = "wind_direction_unit"
        override val default get() = Compass
        override val enums: Array<WindDirectionUnit> get() = arrayOf(Degree, Compass)
    }

    @Serializable
    data object Degree : WindDirectionUnit("°") {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.wind_direction_degree
        override val icon: Int? = null
        override val key: Int = 0
    }

    @Serializable
    data object Compass : WindDirectionUnit("") {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.wind_direction_compass
        override val icon: Int? = null
        override val key: Int = 1
    }
}

@Serializable
data object PercentageUnit : WeatherDataUnit {
    override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.percentage
    override val key: Int = 0
    override val icon: Int? = null
    override val symbol: String = "%"
}