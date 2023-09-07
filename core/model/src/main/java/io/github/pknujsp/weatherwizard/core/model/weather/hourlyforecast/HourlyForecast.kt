package io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast

import androidx.annotation.DrawableRes
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.HumidityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.RainfallValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.SnowfallValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueType
import java.lang.ref.WeakReference
import java.util.Calendar

data class HourlyForecast(
    val items: List<Item>
) : UiModel {
    class Item(
        val dateTime: DateTimeValueType,
        val weatherCondition: WeatherConditionValueType,
        val temperature: TemperatureValueType,
        val feelsLikeTemperature: TemperatureValueType,
        val humidity: HumidityValueType,
        val windSpeed: WindSpeedValueType,
        val windDirection: WindDirectionValueType,
        val rainfallVolume: RainfallValueType = RainfallValueType.none,
        val snowfallVolume: SnowfallValueType = SnowfallValueType.none,
        val rainfallProbability: ProbabilityValueType = ProbabilityValueType.none,
        val snowfallProbability: ProbabilityValueType = ProbabilityValueType.none,
        val precipitationVolume: PrecipitationValueType,
        val precipitationProbability: ProbabilityValueType,
        sunriseSunsetCalculator: SunriseSunsetCalculator,
        currentCalendar: Calendar
    ) {
        @DrawableRes val weatherIcon: Int = sunriseSunsetCalculator.run {
            val sunRise = WeakReference(getOfficialSunriseCalendarForDate(currentCalendar)).get()!!
            val sunSet = WeakReference(getOfficialSunsetCalendarForDate(currentCalendar)).get()!!

            if (sunRise.before(currentCalendar) && sunSet.after(currentCalendar)) {
                weatherCondition.value.dayWeatherIcon
            } else {
                weatherCondition.value.nightWeatherIcon
            }
        }
    }
}