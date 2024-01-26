package io.github.pknujsp.weatherwizard.feature.weather.info.currentweather.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.AirQualityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.HumidityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueType
import java.util.Calendar

@Stable
class CurrentWeather(
    val weatherCondition: WeatherConditionValueType,
    val temperature: TemperatureValueType,
    val feelsLikeTemperature: TemperatureValueType,
    val humidity: HumidityValueType,
    val windSpeed: WindSpeedValueType,
    val windDirection: WindDirectionValueType,
    val precipitationVolume: PrecipitationValueType,
    dayNightCalculator: DayNightCalculator,
    currentCalendar: Calendar
) : UiModel {
    @DrawableRes val weatherIcon: Int =
        weatherCondition.value.getWeatherIconByTimeOfDay(dayNightCalculator.calculate(currentCalendar) == DayNightCalculator.DayNight.DAY)

    var airQuality: AirQualityValueType? by mutableStateOf(null)

}