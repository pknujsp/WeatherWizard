package io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType

data class DailyForecast(
    val dayItems: List<DayItem>
) : UiModel {

    class DayItem {
        private val dateTimes: MutableList<DateTimeValueType> = mutableListOf()
        private val weatherConditions: MutableList<WeatherConditionValueType> = mutableListOf()
        private val precipitationProbabilities: MutableList<ProbabilityValueType> = mutableListOf()

        private var _minTemperature: TemperatureValueType? = null
            set(value) {
                if (field == null) {
                    field = value
                } else {
                    if (value!!.value < field!!.value) {
                        field = value
                    }
                }
            }
        val minTemperature: TemperatureValueType
            get() = TemperatureValueType(_minTemperature!!.value, _minTemperature!!.unit)

        private var _maxTemperature: TemperatureValueType? = null
            set(value) {
                if (field == null) {
                    field = value
                } else {
                    if (value!!.value > field!!.value) {
                        field = value
                    }
                }
            }
        val maxTemperature: TemperatureValueType
            get() = _maxTemperature!!

        fun addValue(
            dateTime: DateTimeValueType,
            weatherCondition: WeatherConditionValueType,
            precipitationProbability: ProbabilityValueType,
            minTemperature: TemperatureValueType,
            maxTemperature: TemperatureValueType
        ) {
            dateTimes.add(dateTime)
            weatherConditions.add(weatherCondition)
            precipitationProbabilities.add(precipitationProbability)

            _minTemperature = minTemperature
            _maxTemperature = maxTemperature
        }

    }
}