package io.github.pknujsp.weatherwizard.core.model.weather.common


import android.content.Context
import io.github.pknujsp.weatherwizard.core.model.R
import io.github.pknujsp.weatherwizard.core.model.airquality.AirQualityDescription

interface NoneValue<T> {
    val none: T
}

interface WeatherValueType<out T : Any> {
    val value: T
}

interface WeatherValueUnitType<T : Any, U : WeatherDataUnit> : WeatherValueType<T> {
    val unit: U
    fun convertUnit(value: T, to: U): T

    fun isNone(): Boolean

    fun toStringWithoutUnit(): String
}

interface WeatherValueNotUnitType<out T : Any> : WeatherValueType<T>


data class DateTimeValueType(
    override val value: String,
) : WeatherValueNotUnitType<String> {

    override fun toString(): String {
        return value
    }

}


data class WeatherConditionValueType(
    override val value: WeatherConditionCategory,
) : WeatherValueNotUnitType<WeatherConditionCategory> {

}

data class TemperatureValueType(
    override val value: Double,
    override val unit: TemperatureUnit,
) : WeatherValueUnitType<Double, TemperatureUnit> {

    companion object : NoneValue<TemperatureValueType> {
        override val none: TemperatureValueType = TemperatureValueType(Double.MIN_VALUE, TemperatureUnit.Celsius)
    }

    override fun convertUnit(value: Double, to: TemperatureUnit): Double {
        return when (unit to to) {
            TemperatureUnit.Celsius to TemperatureUnit.Fahrenheit -> value * 9 / 5 + 32
            TemperatureUnit.Fahrenheit to TemperatureUnit.Celsius -> (value - 32) * 5 / 9
            else -> value
        }
    }


    override fun isNone(): Boolean {
        return value == Double.MIN_VALUE
    }

    override fun toStringWithoutUnit(): String {
        return if (isNone()) "" else value.toInt().toString()
    }

    override fun toString(): String {
        return "${value.toInt()}${unit.symbol}"
    }
}

data class WindSpeedValueType(
    override val value: Double,
    override val unit: WindSpeedUnit,
) : WeatherValueUnitType<Double, WindSpeedUnit> {

    companion object : NoneValue<WindSpeedValueType> {
        override val none: WindSpeedValueType = WindSpeedValueType(Double.MIN_VALUE, WindSpeedUnit.KilometerPerHour)

        private val beaufortScale = listOf(
            0.0 to R.string.wind_strength_0,
            1.0 to R.string.wind_strength_1,
            5.0 to R.string.wind_strength_2,
            12.0 to R.string.wind_strength_3,
            20.0 to R.string.wind_strength_4,
            29.0 to R.string.wind_strength_5,
            39.0 to R.string.wind_strength_6,
            50.0 to R.string.wind_strength_7,
            62.0 to R.string.wind_strength_8,
            75.0 to R.string.wind_strength_9,
            89.0 to R.string.wind_strength_10,
            103.0 to R.string.wind_strength_11,
            Double.MAX_VALUE to R.string.wind_strength_12,
        )
    }

    /**
     * https://en.wikipedia.org/wiki/Beaufort_scale
     */
    fun strength(context: Context): String {
        val kmh = convertUnit(value, WindSpeedUnit.KilometerPerHour)
        val id = beaufortScale.find { (speed, _) -> kmh < speed }!!.second
        return context.getString(id)
    }

    override fun convertUnit(value: Double, to: WindSpeedUnit): Double {
        return when (unit to to) {
            WindSpeedUnit.KilometerPerHour to WindSpeedUnit.MeterPerSecond -> value / 3.6
            WindSpeedUnit.MeterPerSecond to WindSpeedUnit.KilometerPerHour -> value * 3.6
            else -> value
        }
    }


    override fun toString(): String {
        return "$value${unit.symbol}"
    }

    override fun toStringWithoutUnit(): String {
        return if (isNone()) "" else value.toInt().toString()
    }

    override fun isNone(): Boolean {
        return value == Double.MIN_VALUE
    }
}

data class WindDirectionValueType(
    override val value: Int,
    override val unit: WindDirectionUnit,
) : WeatherValueUnitType<Int, WindDirectionUnit> {

    companion object : NoneValue<WindDirectionValueType> {
        override val none: WindDirectionValueType = WindDirectionValueType(Int.MIN_VALUE, WindDirectionUnit.Degree)

    }

    override fun convertUnit(value: Int, to: WindDirectionUnit): Int {
        return value
    }


    override fun toString(): String {
        return "$value${unit.symbol}"
    }

    override fun toStringWithoutUnit(): String {
        return if (isNone()) "" else value.toString()
    }

    override fun isNone(): Boolean {
        return value == Int.MIN_VALUE
    }
}

data class HumidityValueType(
    override val value: Int,
    override val unit: PercentageUnit,
) : WeatherValueUnitType<Int, PercentageUnit> {

    companion object : NoneValue<HumidityValueType> {
        override val none: HumidityValueType = HumidityValueType(Int.MIN_VALUE, PercentageUnit)
    }

    override fun convertUnit(value: Int, to: PercentageUnit): Int {
        return value
    }

    override fun toString(): String {
        return "$value${unit.symbol}"
    }

    override fun toStringWithoutUnit(): String {
        return if (isNone()) "" else value.toString()
    }

    override fun isNone(): Boolean {
        return value == Int.MIN_VALUE
    }
}


data class PressureValueType(
    override val value: Int,
    override val unit: PressureUnit,
) : WeatherValueUnitType<Int, PressureUnit> {

    companion object : NoneValue<PressureValueType> {
        override val none: PressureValueType = PressureValueType(Int.MIN_VALUE, PressureUnit.Hectopascal)


        private val pressureScale = listOf(
            980 to R.string.pressure_very_low,
            1000 to R.string.pressure_low,
            1020 to R.string.pressure_normal,
            1040 to R.string.pressure_high,
            Int.MAX_VALUE to R.string.pressure_very_high,
        )
    }

    fun strength(context: Context): String {
        val hPa = value
        val id = pressureScale.find { (pressure, _) -> hPa < pressure }!!.second
        return context.getString(id)
    }

    override fun convertUnit(value: Int, to: PressureUnit): Int {
        return value
    }


    override fun toString(): String {
        return "$value${unit.symbol}"
    }

    override fun toStringWithoutUnit(): String {
        return if (isNone()) "" else value.toString()
    }

    override fun isNone(): Boolean {
        return value == Int.MIN_VALUE
    }
}

data class VisibilityValueType(
    override val value: Double,
    override val unit: VisibilityUnit,
) : WeatherValueUnitType<Double, VisibilityUnit> {
    companion object : NoneValue<VisibilityValueType> {
        override val none: VisibilityValueType = VisibilityValueType(Double.MIN_VALUE, VisibilityUnit.Kilometer)


        private val visibilityScale = listOf(
            0.0 to R.string.visibility_extremely_low,
            1.0 to R.string.visibility_very_low,
            4.0 to R.string.visibility_low,
            10.0 to R.string.visibility_moderate,
            100.0 to R.string.visibility_high,
            Double.MAX_VALUE to R.string.visibility_very_high,
        )
    }

    fun strength(context: Context): String {
        val km = value
        val id = visibilityScale.find { (visibility, _) -> km < visibility }!!.second
        return context.getString(id)
    }

    override fun convertUnit(value: Double, to: VisibilityUnit): Double {
        return value
    }

    override fun toString(): String {
        return "$value${unit.symbol}"
    }

    override fun isNone(): Boolean {
        return value == Double.MIN_VALUE
    }

    override fun toStringWithoutUnit(): String {
        return if (isNone()) "" else value.toString()
    }
}

data class PrecipitationValueType(
    override val value: Double,
    override val unit: PrecipitationUnit,
) : WeatherValueUnitType<Double, PrecipitationUnit> {

    companion object : NoneValue<PrecipitationValueType> {
        override val none: PrecipitationValueType = PrecipitationValueType(Double.NaN, PrecipitationUnit.Millimeter)

    }

    override fun convertUnit(value: Double, to: PrecipitationUnit): Double {
        return when (unit to to) {
            PrecipitationUnit.Millimeter to PrecipitationUnit.Centimeter -> value / 10
            PrecipitationUnit.Centimeter to PrecipitationUnit.Millimeter -> value * 10
            else -> value
        }
    }

    override fun toString(): String {
        return "$value${unit.symbol}"
    }

    override fun isNone(): Boolean {
        return value.isNaN()
    }

    override fun toStringWithoutUnit(): String {
        return if (isNone()) "" else value.toString()
    }
}

data class SnowfallValueType(
    override val value: Double,
    override val unit: PrecipitationUnit,
) : WeatherValueUnitType<Double, PrecipitationUnit> {
    companion object : NoneValue<SnowfallValueType> {
        override val none: SnowfallValueType = SnowfallValueType(Double.MIN_VALUE, PrecipitationUnit.Millimeter)

        private val snowfallScale = listOf(
            0.0 to R.string.snowfall_none,
            2.5 to R.string.snowfall_light,
            7.6 to R.string.snowfall_moderate,
            15.2 to R.string.snowfall_heavy,
            Double.MAX_VALUE to R.string.snowfall_very_heavy,
        )
    }

    fun strength(context: Context): String {
        val cm = convertUnit(value, PrecipitationUnit.Centimeter)
        val id = snowfallScale.find { (snowfall, _) -> cm < snowfall }!!.second
        return context.getString(id)
    }

    override fun convertUnit(value: Double, to: PrecipitationUnit): Double {
        return when (unit to to) {
            PrecipitationUnit.Millimeter to PrecipitationUnit.Centimeter -> value / 10
            PrecipitationUnit.Centimeter to PrecipitationUnit.Millimeter -> value * 10
            else -> value
        }
    }


    override fun toString(): String {
        return "$value${unit.symbol}"
    }

    override fun isNone(): Boolean {
        return value == Double.MIN_VALUE
    }

    override fun toStringWithoutUnit(): String {
        return if (isNone()) "" else value.toString()
    }
}

data class RainfallValueType(
    override val value: Double,
    override val unit: PrecipitationUnit,
) : WeatherValueUnitType<Double, PrecipitationUnit> {

    companion object : NoneValue<RainfallValueType> {
        override val none: RainfallValueType = RainfallValueType(Double.MIN_VALUE, PrecipitationUnit.Millimeter)

        private val rainfallScale = listOf(
            0.0 to R.string.rainfall_none,
            1.0 to R.string.rainfall_very_light,
            4.0 to R.string.rainfall_light,
            10.0 to R.string.rainfall_moderate,
            50.0 to R.string.rainfall_heavy,
            Double.MAX_VALUE to R.string.rainfall_very_heavy,
        )
    }

    fun strength(context: Context): String {
        val mm = convertUnit(value, PrecipitationUnit.Millimeter)
        val id = rainfallScale.find { (rainfall, _) -> mm < rainfall }!!.second
        return context.getString(id)
    }

    override fun convertUnit(value: Double, to: PrecipitationUnit): Double {
        return when (unit to to) {
            PrecipitationUnit.Millimeter to PrecipitationUnit.Centimeter -> value / 10
            PrecipitationUnit.Centimeter to PrecipitationUnit.Millimeter -> value * 10
            else -> value
        }
    }


    override fun toString(): String {
        return "$value${unit.symbol}"
    }

    override fun toStringWithoutUnit(): String {
        return if (isNone()) "" else value.toString()
    }

    override fun isNone(): Boolean {
        return value == Double.MIN_VALUE
    }
}


data class ProbabilityValueType(
    override val value: Int,
    override val unit: PercentageUnit,
) : WeatherValueUnitType<Int, PercentageUnit> {

    companion object : NoneValue<ProbabilityValueType> {
        override val none: ProbabilityValueType = ProbabilityValueType(Int.MIN_VALUE, PercentageUnit)
    }

    override fun convertUnit(value: Int, to: PercentageUnit): Int {
        return value
    }

    override fun toString(): String {
        return if (isNone()) "-" else "$value${unit.symbol}"
    }

    override fun isNone(): Boolean {
        return value == Int.MIN_VALUE
    }

    override fun toStringWithoutUnit(): String {
        return if (isNone()) "" else value.toString()
    }
}

data class AirQualityValueType(
    override val value: Int,
    val airQualityDescription: AirQualityDescription
) : WeatherValueNotUnitType<Int> {

    override fun toString(): String {
        return value.toString()
    }

}