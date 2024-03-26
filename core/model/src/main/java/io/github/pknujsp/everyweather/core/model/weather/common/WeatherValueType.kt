package io.github.pknujsp.everyweather.core.model.weather.common

import android.content.Context
import androidx.annotation.StringRes
import io.github.pknujsp.everyweather.core.model.airquality.AirQualityDescription
import io.github.pknujsp.everyweather.core.resource.R
import kotlinx.serialization.Serializable

interface NoneValue<T> {
    val None: T
}

interface WeatherValueType<out T : Any> {
    val value: T
}

interface WeatherValueUnitType<T : Any, U : WeatherDataUnit> : WeatherValueType<T> {
    val unit: U
    val isNone: Boolean
    fun convertUnit(to: U): WeatherValueUnitType<T, U>
    fun toStringWithoutUnit(): String
}

interface WeatherValueNotUnitType<out T : Any> : WeatherValueType<T>

@Serializable
data class DateTimeValueType(
    override val value: String,
) : WeatherValueNotUnitType<String> {
    override fun toString(): String {
        return value
    }
}

@Serializable
data class DateValueType(
    override val value: String,
) : WeatherValueNotUnitType<String> {
    override fun toString(): String {
        return value
    }
}

@Serializable
data class WeatherConditionValueType(
    override val value: WeatherConditionCategory,
) : WeatherValueNotUnitType<WeatherConditionCategory>

@Serializable
data class TemperatureValueType(
    override val value: Short,
    override val unit: TemperatureUnit,
) : WeatherValueUnitType<Short, TemperatureUnit> {

    companion object : NoneValue<TemperatureValueType> {
        override val None: TemperatureValueType = TemperatureValueType(Short.MIN_VALUE, TemperatureUnit.default)
    }

    override fun convertUnit(to: TemperatureUnit): TemperatureValueType {
        if (isNone) return this

        return when (unit to to) {
            TemperatureUnit.Celsius to TemperatureUnit.Fahrenheit -> (value * 9 / 5 + 32).toShort()
            TemperatureUnit.Fahrenheit to TemperatureUnit.Celsius -> ((value - 32) * 5 / 9).toShort()
            else -> value
        }.run {
            TemperatureValueType(this, to)
        }
    }

    override val isNone: Boolean
        get() = value == Short.MIN_VALUE

    fun toStringWithOnlyDegree(): String {
        return if (isNone) "" else "$value°"
    }

    override fun toStringWithoutUnit(): String = if (isNone) "" else value.toString()

    override fun toString(): String {
        return if (isNone) "" else "${value}${unit.symbol}"
    }
}

@Serializable
data class WindSpeedValueType(
    override val value: Double,
    override val unit: WindSpeedUnit,
) : WeatherValueUnitType<Double, WindSpeedUnit> {

    override val isNone: Boolean
        get() = value == Double.MIN_VALUE

    companion object : NoneValue<WindSpeedValueType> {

        override val None: WindSpeedValueType = WindSpeedValueType(Double.MIN_VALUE, WindSpeedUnit.default)

        private val beaufortScale = arrayOf(
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

    fun strength(context: Context): String {
        val kmh = convertUnit(WindSpeedUnit.KilometerPerHour).value
        val id = beaufortScale.find { (speed, _) -> kmh < speed }!!.second
        return context.getString(id)
    }

    override fun convertUnit(to: WindSpeedUnit): WindSpeedValueType {
        if (isNone) return this

        return when (unit to to) {
            WindSpeedUnit.KilometerPerHour to WindSpeedUnit.MeterPerSecond -> value / 3.6
            WindSpeedUnit.MeterPerSecond to WindSpeedUnit.KilometerPerHour -> value * 3.6
            else -> value
        }.run {
            WindSpeedValueType(this, to)
        }
    }

    override fun toString(): String {
        return if (isNone) "" else "$value${unit.symbol}"
    }

    override fun toStringWithoutUnit(): String = if (isNone) "" else value.toString()
}

@Serializable
data class WindDirectionValueType(
    override val value: Short,
    override val unit: WindDirectionUnit = WindDirectionUnit.default,
) : WeatherValueUnitType<Short, WindDirectionUnit> {
    companion object : NoneValue<WindDirectionValueType> {
        override val None: WindDirectionValueType = WindDirectionValueType(Short.MIN_VALUE, WindDirectionUnit.default)

        @StringRes private val compassPointStringResources = arrayOf(
            R.string.wind_direction_n,
            R.string.wind_direction_ne,
            R.string.wind_direction_e,
            R.string.wind_direction_se,
            R.string.wind_direction_s,
            R.string.wind_direction_sw,
            R.string.wind_direction_w,
            R.string.wind_direction_nw,
        )

        private val compassPointString = arrayOf(
            "N",
            "NE",
            "E",
            "SE",
            "S",
            "SW",
            "W",
            "NW",
        )
    }

    override val isNone: Boolean
        get() = value == Short.MIN_VALUE

    @get:StringRes val compassStringRes: Int
        get() = compassPointStringResources[(value / 45) % 8]

    val compassString: String
        get() = compassPointString[(value / 45) % 8]

    override fun convertUnit(to: WindDirectionUnit): WindDirectionValueType = this

    override fun toString(): String {
        return if (isNone) "" else "$value${unit.symbol}"
    }

    override fun toStringWithoutUnit(): String {
        return if (isNone) "" else value.toString()
    }
}

@Serializable
data class HumidityValueType(
    override val value: Short,
    override val unit: PercentageUnit = PercentageUnit,
) : WeatherValueUnitType<Short, PercentageUnit> {
    companion object : NoneValue<HumidityValueType> {
        override val None: HumidityValueType = HumidityValueType(Short.MIN_VALUE, PercentageUnit)
    }

    override val isNone: Boolean
        get() = value == Short.MIN_VALUE

    override fun convertUnit(to: PercentageUnit): HumidityValueType {
        if (isNone) return this

        return HumidityValueType(value, to)
    }

    override fun toString(): String {
        return if (isNone) "" else "$value${unit.symbol}"
    }

    override fun toStringWithoutUnit(): String {
        return if (isNone) "" else value.toString()
    }
}

@Serializable
data class PressureValueType(
    override val value: Short,
    override val unit: PressureUnit,
) : WeatherValueUnitType<Short, PressureUnit> {
    companion object : NoneValue<PressureValueType> {
        override val None: PressureValueType = PressureValueType(Short.MIN_VALUE, PressureUnit.default)

        private val pressureScale = arrayOf(
            980 to R.string.pressure_very_low,
            1000 to R.string.pressure_low,
            1020 to R.string.pressure_normal,
            1040 to R.string.pressure_high,
            Int.MAX_VALUE to R.string.pressure_very_high,
        )
    }

    override val isNone: Boolean
        get() = value == Short.MIN_VALUE

    fun strength(context: Context): String {
        val hPa = value
        val id = pressureScale.find { (pressure, _) -> hPa < pressure }!!.second
        return context.getString(id)
    }

    override fun convertUnit(to: PressureUnit): PressureValueType {
        if (isNone) return this

        return PressureValueType(value, to)
    }

    override fun toString(): String {
        return if (isNone) "" else "$value${unit.symbol}"
    }

    override fun toStringWithoutUnit(): String {
        return if (isNone) "" else value.toString()
    }
}

@Serializable
data class VisibilityValueType(
    override val value: Double,
    override val unit: VisibilityUnit,
) : WeatherValueUnitType<Double, VisibilityUnit> {
    companion object : NoneValue<VisibilityValueType> {
        override val None: VisibilityValueType = VisibilityValueType(Double.MIN_VALUE, VisibilityUnit.default)

        private val visibilityScale = listOf(
            0.0 to R.string.visibility_extremely_low,
            1.0 to R.string.visibility_very_low,
            4.0 to R.string.visibility_low,
            10.0 to R.string.visibility_moderate,
            100.0 to R.string.visibility_high,
            Double.MAX_VALUE to R.string.visibility_very_high,
        )
    }

    override val isNone: Boolean
        get() = value == Double.MIN_VALUE

    fun strength(context: Context): String {
        val km = value
        val id = visibilityScale.find { (visibility, _) -> km < visibility }!!.second
        return context.getString(id)
    }

    override fun convertUnit(to: VisibilityUnit): VisibilityValueType {
        if (isNone) return this

        return VisibilityValueType(value, to)
    }

    override fun toString(): String {
        return if (isNone) "" else "$value${unit.symbol}"
    }


    override fun toStringWithoutUnit(): String {
        return if (isNone) "" else value.toString()
    }
}

@Serializable
data class PrecipitationValueType(
    override val value: Double,
    override val unit: PrecipitationUnit,
) : WeatherValueUnitType<Double, PrecipitationUnit> {
    companion object : NoneValue<PrecipitationValueType> {
        override val None: PrecipitationValueType = PrecipitationValueType(Double.MIN_VALUE, PrecipitationUnit.default)
        val rainDrop = PrecipitationValueType(0.1, PrecipitationUnit.Millimeter)
        val snowDrop = PrecipitationValueType(0.1, PrecipitationUnit.Millimeter)
    }

    override val isNone: Boolean
        get() = value == Double.MIN_VALUE

    override fun convertUnit(to: PrecipitationUnit): PrecipitationValueType {
        if (isNone) return None

        return when (unit to to) {
            PrecipitationUnit.Millimeter to PrecipitationUnit.Centimeter -> value / 10.0
            PrecipitationUnit.Centimeter to PrecipitationUnit.Millimeter -> value * 10.0
            else -> value
        }.run {
            PrecipitationValueType(this, to)
        }
    }

    override fun toString(): String {
        return if (isNone) "" else "$value${unit.symbol}"
    }


    override fun toStringWithoutUnit(): String = if (isNone) "" else value.toString()
}

@Serializable
data class SnowfallValueType(
    override val value: Double,
    override val unit: PrecipitationUnit,
) : WeatherValueUnitType<Double, PrecipitationUnit> {
    companion object : NoneValue<SnowfallValueType> {
        override val None: SnowfallValueType = SnowfallValueType(Double.MIN_VALUE, PrecipitationUnit.default)

        private val snowfallScale = listOf(
            0.0 to R.string.snowfall_none,
            2.5 to R.string.snowfall_light,
            7.6 to R.string.snowfall_moderate,
            15.2 to R.string.snowfall_heavy,
            Double.MAX_VALUE to R.string.snowfall_very_heavy,
        )
    }

    override val isNone: Boolean
        get() = value == Double.MIN_VALUE

    fun strength(context: Context): String {
        val cm = convertUnit(PrecipitationUnit.Centimeter).value
        val id = snowfallScale.find { (snowfall, _) -> cm < snowfall }!!.second
        return context.getString(id)
    }

    override fun convertUnit(to: PrecipitationUnit): SnowfallValueType {
        if (isNone) return this

        return when (unit to to) {
            PrecipitationUnit.Millimeter to PrecipitationUnit.Centimeter -> value / 10
            PrecipitationUnit.Centimeter to PrecipitationUnit.Millimeter -> value * 10
            else -> value
        }.run {
            SnowfallValueType(this, to)
        }
    }

    override fun toString(): String {
        return if (isNone) "" else "$value${unit.symbol}"
    }

    override fun toStringWithoutUnit(): String {
        return if (isNone) "" else value.toString()
    }
}

@Serializable
data class RainfallValueType(
    override val value: Double,
    override val unit: PrecipitationUnit,
) : WeatherValueUnitType<Double, PrecipitationUnit> {
    companion object : NoneValue<RainfallValueType> {
        override val None: RainfallValueType = RainfallValueType(Double.MIN_VALUE, PrecipitationUnit.default)

        private val rainfallScale = listOf(
            0.0 to R.string.rainfall_none,
            1.0 to R.string.rainfall_very_light,
            4.0 to R.string.rainfall_light,
            10.0 to R.string.rainfall_moderate,
            50.0 to R.string.rainfall_heavy,
            Double.MAX_VALUE to R.string.rainfall_very_heavy,
        )
    }

    override val isNone: Boolean
        get() = value == Double.MIN_VALUE

    fun strength(context: Context): String {
        val mm = convertUnit(PrecipitationUnit.Millimeter).value
        val id = rainfallScale.find { (rainfall, _) -> mm < rainfall }!!.second
        return context.getString(id)
    }

    override fun convertUnit(to: PrecipitationUnit): RainfallValueType {
        if (isNone) return this

        return when (unit to to) {
            PrecipitationUnit.Millimeter to PrecipitationUnit.Centimeter -> value / 10
            PrecipitationUnit.Centimeter to PrecipitationUnit.Millimeter -> value * 10
            else -> value
        }.run {
            RainfallValueType(this, to)
        }
    }

    override fun toString(): String {
        return if (isNone) "" else "$value${unit.symbol}"
    }

    override fun toStringWithoutUnit(): String {
        return if (isNone) "" else value.toString()
    }
}

@Serializable
data class ProbabilityValueType(
    override val value: Short,
    override val unit: PercentageUnit = PercentageUnit,
) : WeatherValueUnitType<Short, PercentageUnit> {
    companion object : NoneValue<ProbabilityValueType> {
        override val None: ProbabilityValueType = ProbabilityValueType(Short.MIN_VALUE, PercentageUnit)
    }

    override val isNone: Boolean
        get() = value == Short.MIN_VALUE

    override fun convertUnit(to: PercentageUnit): ProbabilityValueType {
        if (isNone) return this

        return ProbabilityValueType(value, to)
    }

    override fun toString(): String {
        return if (isNone) "" else "$value${unit.symbol}"
    }


    override fun toStringWithoutUnit(): String {
        return if (isNone) "" else value.toString()
    }
}

@Serializable
data class AirQualityValueType(
    override val value: Short,
    val airQualityDescription: AirQualityDescription,
) : WeatherValueNotUnitType<Short> {
    override fun toString(): String {
        return value.toString()
    }
}