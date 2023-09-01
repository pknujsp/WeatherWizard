package io.github.pknujsp.weatherwizard.core.model.weather.common

interface WeatherDataType {
    val value: Any
    fun isEmpty(): Boolean

    fun toInt(): Int = value.toString().toInt()

    fun toDouble(): Double = value.toString().toDouble()

    fun toFloat(): Float = value.toString().toFloat()

}

fun Double.toTemperatureType() = TemperatureType(this)

fun Double.toHumidityType() = HumidityType(this)

fun Double.toWindSpeedType() = WindSpeedType(this)

fun Double.toWindDirectionType() = WindDirectionType(this)

fun Double.toWindDirectionDegreeType() = WindDirectionDegreeType(this)

fun Double.toAirQualityType() = AirQualityType(this)

fun Double.toPressureType() = PressureType(this)

fun Double.toVisibilityType() = VisibilityType(this)

fun Double.toUVIndexType() = UVIndexType(this)

fun Double.toDewPointType() = DewPointType(this)

fun Double.toCloudinessType() = CloudinessType(this)

fun Double.toPrecipitationVolumeType() = PrecipitationVolumeType(this)

fun Double.toPrecipitationProbabilityType() = PrecipitationProbabilityType(this)

fun Int.toContaminantConcentrationType() = ContaminantConcentrationType(this)

fun String.toWeatherConditionType() = WeatherConditionType(this)

fun String.toDateType() = DateType(this)

fun String.toDateTimeType() = DateTimeType(this)

data class WeatherDoubleValueType(override val value: Double) : Comparable<Double>, WeatherDataType {
    companion object {
        fun emptyValue() = WeatherDoubleValueType(Double.NaN)
    }

    override fun isEmpty() = value.isNaN()
    override fun compareTo(other: Double): Int = value.compareTo(other)

}

data class WeatherIntValueType(override val value: Int) : Comparable<Int>, WeatherDataType {
    companion object {
        fun emptyValue() = WeatherIntValueType(Int.MIN_VALUE)
    }

    override fun isEmpty() = value == Int.MIN_VALUE
    override fun compareTo(other: Int): Int = value.compareTo(other)

}

data class WeatherTextValueType(override val value: String) : WeatherDataType {
    companion object {
        fun emptyValue() = WeatherTextValueType("")
    }

    override fun isEmpty() = value.isEmpty()
}

typealias TemperatureType = WeatherDoubleValueType
typealias HumidityType = WeatherDoubleValueType
typealias WindSpeedType = WeatherDoubleValueType
typealias WindDirectionType = WeatherDoubleValueType
typealias WindDirectionDegreeType = WeatherDoubleValueType
typealias AirQualityType = WeatherDoubleValueType
typealias PressureType = WeatherDoubleValueType
typealias VisibilityType = WeatherDoubleValueType
typealias UVIndexType = WeatherDoubleValueType
typealias DewPointType = WeatherDoubleValueType
typealias CloudinessType = WeatherDoubleValueType
typealias PrecipitationVolumeType = WeatherDoubleValueType
typealias PrecipitationProbabilityType = WeatherDoubleValueType

typealias ContaminantConcentrationType = WeatherIntValueType

typealias WeatherConditionType = WeatherTextValueType
typealias DateType = WeatherTextValueType
typealias DateTimeType = WeatherTextValueType

data class Coordinate(
    val latitude: Double,
    val longitude: Double
)