package io.github.pknujsp.weatherwizard.core.model.weather.common

interface WeatherDataType {
    fun isEmpty(): Boolean
}

data class WeatherDoubleValueType(val value: Double) : Comparable<Double>, WeatherDataType {
    companion object {
        fun emptyValue() = WeatherDoubleValueType(Double.NaN)
    }

    override fun isEmpty() = value.isNaN()
    override fun compareTo(other: Double): Int = value.compareTo(other)
}

data class WeatherIntValueType(val value: Int) : Comparable<Int>, WeatherDataType {
    companion object {
        fun emptyValue() = WeatherIntValueType(Int.MIN_VALUE)
    }

    override fun isEmpty() = value == Int.MIN_VALUE
    override fun compareTo(other: Int): Int = value.compareTo(other)
}

data class WeatherTextValueType(val value: String) : WeatherDataType {
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

typealias PrecipitationCategoryType = WeatherTextValueType
typealias WeatherConditionType = WeatherTextValueType
typealias DateType = WeatherTextValueType
typealias DateTimeType = WeatherTextValueType

data class Coordinate(
    val latitude: Double,
    val longitude: Double
)