package io.github.pknujsp.everyweather.feature.weather.info.currentweather.model

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.pknujsp.everyweather.core.common.util.DayNightCalculator
import io.github.pknujsp.everyweather.core.model.UiModel
import io.github.pknujsp.everyweather.core.model.weather.common.AirQualityValueType
import io.github.pknujsp.everyweather.core.model.weather.common.HumidityValueType
import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WindSpeedValueType
import io.github.pknujsp.everyweather.core.resource.R
import java.util.Calendar
import kotlin.math.absoluteValue

@Stable
class CurrentWeather(
    val weatherCondition: WeatherConditionValueType,
    val temperature: TemperatureValueType,
    val feelsLikeTemperature: TemperatureValueType,
    val humidity: HumidityValueType,
    val windSpeed: WindSpeedValueType,
    val windDirection: WindDirectionValueType,
    val precipitationVolume: PrecipitationValueType,
    val yesterdayTemperature: TemperatureValueType? = null,
    dayNightCalculator: DayNightCalculator,
    currentCalendar: Calendar
) : UiModel {
    @DrawableRes val weatherIcon: Int =
        weatherCondition.value.getWeatherIconByTimeOfDay(dayNightCalculator.calculate(currentCalendar) == DayNightCalculator.DayNight.DAY)

    fun text(todayTemperature: TemperatureValueType, context: Context): List<String> {
        val diffTemperature =
            TemperatureValueType((todayTemperature.value - yesterdayTemperature!!.value).toShort(), todayTemperature.unit)

        val text =
            context.getString(if (diffTemperature.value.toInt() == 0) io.github.pknujsp.everyweather.core.resource.R.string.as_yesterday else io.github.pknujsp.everyweather.core.resource.R.string.than_yesterday)

        val endText =
            context.getString(if (diffTemperature.value.toInt() > 0) io.github.pknujsp.everyweather.core.resource.R.string.higher else if (diffTemperature.value.toInt() < 0) io.github.pknujsp.everyweather.core.resource.R.string.lower else io.github.pknujsp.everyweather.core.resource.R.string.is_)

        val temp = if (diffTemperature.value.toInt() == 0) {
            context.getString(R.string.same_temperature)
        } else {
            "${diffTemperature.value}${diffTemperature.unit.symbol}"
        }

        return listOf(text, temp, endText)
    }

}