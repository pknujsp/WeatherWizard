package io.github.pknujsp.weatherwizard.core.model.weather.common


import android.content.Context
import androidx.annotation.DrawableRes
import io.github.pknujsp.weatherwizard.core.model.R

interface WeatherValue

abstract class WeatherValueClass : WeatherValue {
    abstract val value: WeatherDoubleValueType
    abstract val unit: WeatherDataUnit

    fun formattedIntValue(): String = "${value.toInt()}${unit.symbol}"
    fun formattedDoubleValue(): String = "${value * 10 / 10.0}${unit.symbol}"
}

data class WeatherConditionValueClass(
    @DrawableRes val weatherIcon: Int,
    val weatherCondition: String,
) : WeatherValue

data class TemperatureValueClass(
    override val value: TemperatureType,
    override val unit: TemperatureUnit,
) : WeatherValueClass()

data class WindSpeedValueClass(
    override val value: WindSpeedType,
    override val unit: WindSpeedUnit,
) : WeatherValueClass() {

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
        val kmh = unit.convert(value, WindSpeedUnit.KilometerPerHour)
        val id = beaufortScale.find { (speed, _) -> kmh < speed }!!.second
        return context.getString(id)
    }
}

data class WindDirectionValueClass(
    override val value: WindDirectionType,
    override val unit: WindDirectionUnit,
) : WeatherValueClass()

data class HumidityValueClass(
    override val value: HumidityType,
    override val unit: PercentUnit,
) : WeatherValueClass()


data class AirQualityValueClass(
    override val value: AirQualityType,
    override val unit: AirQualityUnit,
) : WeatherValueClass()

data class PressureValueClass(
    override val value: PressureType,
    override val unit: PressureUnit,
) : WeatherValueClass() {

    private companion object {
        val pressureScale = listOf(
            980.0 to R.string.pressure_very_low,
            1000.0 to R.string.pressure_low,
            1020.0 to R.string.pressure_normal,
            1040.0 to R.string.pressure_high,
            Double.MAX_VALUE to R.string.pressure_very_high,
        )
    }

    fun strength(context: Context): String {
        val hPa = unit.convert(value, PressureUnit.Hectopascal)
        val id = pressureScale.find { (pressure, _) -> hPa < pressure }!!.second
        return context.getString(id)
    }
}

data class VisibilityValueClass(
    override val value: VisibilityType,
    override val unit: VisibilityUnit,
) : WeatherValueClass() {
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
        val km = unit.convert(value, VisibilityUnit.Kilometer)
        val id = visibilityScale.find { (visibility, _) -> km < visibility }!!.second
        return context.getString(id)
    }
}

data class UVIndexValueClass(
    override val value: UVIndexType,
    override val unit: UVIndexUnit,
) : WeatherValueClass()

data class DewPointValueClass(
    override val value: DewPointType,
    override val unit: TemperatureUnit,
) : WeatherValueClass()

data class CloudinessValueClass(
    override val value: CloudinessType,
    override val unit: PercentUnit,
) : WeatherValueClass()

data class PrecipitationValueClass(
    override val value: PrecipitationVolumeType,
    override val unit: PrecipitationUnit,
) : WeatherValueClass()

data class SnowfallValueClass(
    override val value: PrecipitationVolumeType,
    override val unit: PrecipitationUnit,
) : WeatherValueClass() {
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
        val cm = unit.convert(value, PrecipitationUnit.Centimeter)
        val id = snowfallScale.find { (snowfall, _) -> cm < snowfall }!!.second
        return context.getString(id)
    }
}

data class RainfallValueClass(
    override val value: PrecipitationVolumeType,
    override val unit: PrecipitationUnit,
) : WeatherValueClass() {
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
        val mm = unit.convert(value, PrecipitationUnit.Millimeter)
        val id = rainfallScale.find { (rainfall, _) -> mm < rainfall }!!.second
        return context.getString(id)
    }
}