package io.github.pknujsp.weatherwizard.core.model.weather.common


import android.content.Context
import androidx.annotation.DrawableRes
import io.github.pknujsp.weatherwizard.core.model.R
import kotlin.reflect.KProperty

interface NoneValue<T> {
    val none: T
}

interface WeatherValueType<out T : Any> {
    val value: T
}

interface WeatherValueUnitType<T : Any, U : WeatherDataUnit> : WeatherValueType<T> {
    val unit: U
    fun convertUnit(value: T, to: U): T
}

interface WeatherValueNotUnitType<out T : Any> : WeatherValueType<T>


data class DateTimeValueType(
    override val value: String,
) : WeatherValueNotUnitType<String> {

    override fun toString(): String {
        return value
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return value
    }
}


data class WeatherConditionValueType(
    @DrawableRes val weatherIcon: Int,
    override val value: String,
) : WeatherValueNotUnitType<String> {

    companion object {
        private val iconMap = mapOf(
            "맑음" to io.github.pknujsp.weatherwizard.core.common.R.drawable.day_clear,
            "구름조금" to io.github.pknujsp.weatherwizard.core.common.R.drawable.day_partly_cloudy,
            "구름많음" to io.github.pknujsp.weatherwizard.core.common.R.drawable.day_mostly_cloudy,
            "흐림" to io.github.pknujsp.weatherwizard.core.common.R.drawable.overcast,
        )

        @DrawableRes
        fun icon(condition: String): Int {
            return iconMap[condition] ?: io.github.pknujsp.weatherwizard.core.common.R.drawable.day_clear
        }
    }

    override fun toString(): String {
        return value
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return value
    }
}

data class TemperatureValueType(
    override val value: Double,
    override val unit: TemperatureUnit,
) : WeatherValueUnitType<Double, TemperatureUnit> {

    override fun convertUnit(value: Double, to: TemperatureUnit): Double {
        return when (unit to to) {
            TemperatureUnit.Celsius to TemperatureUnit.Fahrenheit -> value * 9 / 5 + 32
            TemperatureUnit.Fahrenheit to TemperatureUnit.Celsius -> (value - 32) * 5 / 9
            else -> value
        }
    }


    operator fun plus(other: TemperatureValueType): TemperatureValueType {
        return TemperatureValueType(value + other.value, unit)
    }

    operator fun minus(other: TemperatureValueType): TemperatureValueType {
        return TemperatureValueType(value - other.value, unit)
    }

    operator fun times(other: TemperatureValueType): TemperatureValueType {
        return TemperatureValueType(value * other.value, unit)
    }

    operator fun div(other: TemperatureValueType): TemperatureValueType {
        return TemperatureValueType(value / other.value, unit)
    }

    operator fun rem(other: TemperatureValueType): TemperatureValueType {
        return TemperatureValueType(value % other.value, unit)
    }

    operator fun unaryPlus(): TemperatureValueType {
        return TemperatureValueType(+value, unit)
    }

    operator fun unaryMinus(): TemperatureValueType {
        return TemperatureValueType(-value, unit)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return value
    }
}

data class WindSpeedValueType(
    override val value: Double,
    override val unit: WindSpeedUnit,
) : WeatherValueUnitType<Double, WindSpeedUnit> {

    private companion object {
        val beaufortScale = listOf(
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


    operator fun plus(other: WindSpeedValueType): WindSpeedValueType {
        return WindSpeedValueType(value + other.value, unit)
    }

    operator fun minus(other: WindSpeedValueType): WindSpeedValueType {
        return WindSpeedValueType(value - other.value, unit)
    }

    operator fun times(other: WindSpeedValueType): WindSpeedValueType {
        return WindSpeedValueType(value * other.value, unit)
    }

    operator fun div(other: WindSpeedValueType): WindSpeedValueType {
        return WindSpeedValueType(value / other.value, unit)
    }

    operator fun rem(other: WindSpeedValueType): WindSpeedValueType {
        return WindSpeedValueType(value % other.value, unit)
    }

    operator fun unaryPlus(): WindSpeedValueType {
        return WindSpeedValueType(+value, unit)
    }

    operator fun unaryMinus(): WindSpeedValueType {
        return WindSpeedValueType(-value, unit)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return value
    }
}

data class WindDirectionValueType(
    override val value: Int,
    override val unit: WindDirectionUnit,
) : WeatherValueUnitType<Int, WindDirectionUnit> {
    override fun convertUnit(value: Int, to: WindDirectionUnit): Int {
        return value
    }

    operator fun plus(other: WindDirectionValueType): WindDirectionValueType {
        return WindDirectionValueType(value + other.value, unit)
    }

    operator fun minus(other: WindDirectionValueType): WindDirectionValueType {
        return WindDirectionValueType(value - other.value, unit)
    }

    operator fun times(other: WindDirectionValueType): WindDirectionValueType {
        return WindDirectionValueType(value * other.value, unit)
    }

    operator fun div(other: WindDirectionValueType): WindDirectionValueType {
        return WindDirectionValueType(value / other.value, unit)
    }

    operator fun rem(other: WindDirectionValueType): WindDirectionValueType {
        return WindDirectionValueType(value % other.value, unit)
    }

    operator fun unaryPlus(): WindDirectionValueType {
        return WindDirectionValueType(+value, unit)
    }

    operator fun unaryMinus(): WindDirectionValueType {
        return WindDirectionValueType(-value, unit)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return value
    }
}

data class HumidityValueType(
    override val value: Int,
    override val unit: PercentageUnit,
) : WeatherValueUnitType<Int, PercentageUnit> {

    companion object : NoneValue<Int> {
        override val none: Int = Int.MAX_VALUE
    }

    override fun convertUnit(value: Int, to: PercentageUnit): Int {
        return value
    }

    operator fun plus(other: HumidityValueType): HumidityValueType {
        return HumidityValueType(value + other.value, unit)
    }

    operator fun minus(other: HumidityValueType): HumidityValueType {
        return HumidityValueType(value - other.value, unit)
    }

    operator fun times(other: HumidityValueType): HumidityValueType {
        return HumidityValueType(value * other.value, unit)
    }

    operator fun div(other: HumidityValueType): HumidityValueType {
        return HumidityValueType(value / other.value, unit)
    }

    operator fun rem(other: HumidityValueType): HumidityValueType {
        return HumidityValueType(value % other.value, unit)
    }

    operator fun unaryPlus(): HumidityValueType {
        return HumidityValueType(+value, unit)
    }

    operator fun unaryMinus(): HumidityValueType {
        return HumidityValueType(-value, unit)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return value
    }

}


data class PressureValueType(
    override val value: Int,
    override val unit: PressureUnit,
) : WeatherValueUnitType<Int, PressureUnit> {

    private companion object {
        val pressureScale = listOf(
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

    operator fun plus(other: PressureValueType): PressureValueType {
        return PressureValueType(value + other.value, unit)
    }

    operator fun minus(other: PressureValueType): PressureValueType {
        return PressureValueType(value - other.value, unit)
    }

    operator fun times(other: PressureValueType): PressureValueType {
        return PressureValueType(value * other.value, unit)
    }

    operator fun div(other: PressureValueType): PressureValueType {
        return PressureValueType(value / other.value, unit)
    }

    operator fun rem(other: PressureValueType): PressureValueType {
        return PressureValueType(value % other.value, unit)
    }

    operator fun unaryPlus(): PressureValueType {
        return PressureValueType(+value, unit)
    }

    operator fun unaryMinus(): PressureValueType {
        return PressureValueType(-value, unit)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return value
    }
}

data class VisibilityValueType(
    override val value: Double,
    override val unit: VisibilityUnit,
) : WeatherValueUnitType<Double, VisibilityUnit> {
    private companion object {
        val visibilityScale = listOf(
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

    operator fun plus(other: VisibilityValueType): VisibilityValueType {
        return VisibilityValueType(value + other.value, unit)
    }

    operator fun minus(other: VisibilityValueType): VisibilityValueType {
        return VisibilityValueType(value - other.value, unit)
    }

    operator fun times(other: VisibilityValueType): VisibilityValueType {
        return VisibilityValueType(value * other.value, unit)
    }

    operator fun div(other: VisibilityValueType): VisibilityValueType {
        return VisibilityValueType(value / other.value, unit)
    }

    operator fun rem(other: VisibilityValueType): VisibilityValueType {
        return VisibilityValueType(value % other.value, unit)
    }

    operator fun unaryPlus(): VisibilityValueType {
        return VisibilityValueType(+value, unit)
    }

    operator fun unaryMinus(): VisibilityValueType {
        return VisibilityValueType(-value, unit)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return value
    }
}

data class PrecipitationValueType(
    override val value: Double,
    override val unit: PrecipitationUnit,
) : WeatherValueUnitType<Double, PrecipitationUnit> {

    override fun convertUnit(value: Double, to: PrecipitationUnit): Double {
        return when (unit to to) {
            PrecipitationUnit.Millimeter to PrecipitationUnit.Centimeter -> value / 10
            PrecipitationUnit.Centimeter to PrecipitationUnit.Millimeter -> value * 10
            else -> value
        }
    }

    operator fun plus(other: PrecipitationValueType): PrecipitationValueType {
        return PrecipitationValueType(value + other.value, unit)
    }

    operator fun minus(other: PrecipitationValueType): PrecipitationValueType {
        return PrecipitationValueType(value - other.value, unit)
    }

    operator fun times(other: PrecipitationValueType): PrecipitationValueType {
        return PrecipitationValueType(value * other.value, unit)
    }

    operator fun div(other: PrecipitationValueType): PrecipitationValueType {
        return PrecipitationValueType(value / other.value, unit)
    }

    operator fun rem(other: PrecipitationValueType): PrecipitationValueType {
        return PrecipitationValueType(value % other.value, unit)
    }

    operator fun unaryPlus(): PrecipitationValueType {
        return PrecipitationValueType(+value, unit)
    }

    operator fun unaryMinus(): PrecipitationValueType {
        return PrecipitationValueType(-value, unit)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return value
    }
}

data class SnowfallValueType(
    override val value: Double,
    override val unit: PrecipitationUnit,
) : WeatherValueUnitType<Double, PrecipitationUnit> {
    private companion object {
        val snowfallScale = listOf(
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

    operator fun plus(other: SnowfallValueType): SnowfallValueType {
        return SnowfallValueType(value + other.value, unit)
    }

    operator fun minus(other: SnowfallValueType): SnowfallValueType {
        return SnowfallValueType(value - other.value, unit)
    }

    operator fun times(other: SnowfallValueType): SnowfallValueType {
        return SnowfallValueType(value * other.value, unit)
    }

    operator fun div(other: SnowfallValueType): SnowfallValueType {
        return SnowfallValueType(value / other.value, unit)
    }

    operator fun rem(other: SnowfallValueType): SnowfallValueType {
        return SnowfallValueType(value % other.value, unit)
    }

    operator fun unaryPlus(): SnowfallValueType {
        return SnowfallValueType(+value, unit)
    }

    operator fun unaryMinus(): SnowfallValueType {
        return SnowfallValueType(-value, unit)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return value
    }
}

data class RainfallValueType(
    override val value: Double,
    override val unit: PrecipitationUnit,
) : WeatherValueUnitType<Double, PrecipitationUnit> {
    private companion object {
        val rainfallScale = listOf(
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

    operator fun plus(other: RainfallValueType): RainfallValueType {
        return RainfallValueType(value + other.value, unit)
    }

    operator fun minus(other: RainfallValueType): RainfallValueType {
        return RainfallValueType(value - other.value, unit)
    }

    operator fun times(other: RainfallValueType): RainfallValueType {
        return RainfallValueType(value * other.value, unit)
    }

    operator fun div(other: RainfallValueType): RainfallValueType {
        return RainfallValueType(value / other.value, unit)
    }

    operator fun rem(other: RainfallValueType): RainfallValueType {
        return RainfallValueType(value % other.value, unit)
    }

    operator fun unaryPlus(): RainfallValueType {
        return RainfallValueType(+value, unit)
    }

    operator fun unaryMinus(): RainfallValueType {
        return RainfallValueType(-value, unit)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return value
    }
}


data class ProbabilityValueType(
    override val value: Int,
    override val unit: PercentageUnit,
) : WeatherValueUnitType<Int, PercentageUnit> {


    override fun convertUnit(value: Int, to: PercentageUnit): Int {
        return value
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return value
    }
}