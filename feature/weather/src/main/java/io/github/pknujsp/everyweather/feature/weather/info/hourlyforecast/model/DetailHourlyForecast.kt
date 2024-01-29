package io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import io.github.pknujsp.everyweather.core.common.util.DayNightCalculator
import io.github.pknujsp.everyweather.core.common.util.toCalendar
import io.github.pknujsp.everyweather.core.model.UiModel
import io.github.pknujsp.everyweather.core.model.weather.common.DateTimeValueType
import io.github.pknujsp.everyweather.core.model.weather.common.HumidityValueType
import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.everyweather.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.everyweather.core.model.weather.common.RainfallValueType
import io.github.pknujsp.everyweather.core.model.weather.common.SnowfallValueType
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WindSpeedValueType
import java.time.ZonedDateTime

@Stable
class DetailHourlyForecast(
    val items: List<Pair<Header, List<Item>>>
) : UiModel {

    val displayRainfallVolume = items.any { it.second.any { item -> item.rainfallVolume.isNotEmpty() } }
    val displaySnowfallVolume = items.any { it.second.any { item -> item.snowfallVolume.isNotEmpty() } }
    val displayPrecipitationVolume = items.any { it.second.any { item -> item.precipitationVolume.isNotEmpty() } }
    val displayPrecipitationProbability = items.any { it.second.any { item -> item.precipitationProbability != "-" } }

    class Header(
        val id: Int,
        val title: String,
    ) : UiModel

    class Item(
        val id: Int,
        weatherCondition: WeatherConditionValueType,
        dateTime: DateTimeValueType,
        temperature: TemperatureValueType,
        feelsLikeTemperature: TemperatureValueType,
        humidity: HumidityValueType,
        windSpeed: WindSpeedValueType,
        windDirection: WindDirectionValueType,
        rainfallVolume: RainfallValueType = RainfallValueType.none,
        snowfallVolume: SnowfallValueType = SnowfallValueType.none,
        rainfallProbability: ProbabilityValueType = ProbabilityValueType.none,
        snowfallProbability: ProbabilityValueType = ProbabilityValueType.none,
        precipitationVolume: PrecipitationValueType,
        precipitationProbability: ProbabilityValueType,
        dayNightCalculator: DayNightCalculator,
    ) : UiModel {

        val temperature: String = temperature.toString()
        val temperatureInt: Int = temperature.value.toInt()
        val precipitationProbability: String = precipitationProbability.toString()
        val precipitationVolume: String = precipitationVolume.toStringWithoutUnit()
        val rainfallVolume: String = rainfallVolume.toStringWithoutUnit()
        val snowfallVolume: String = snowfallVolume.toStringWithoutUnit()

        @DrawableRes val weatherIcon: Int
        val time: String
        @StringRes val weatherCondition: Int = weatherCondition.value.stringRes

        init {
            ZonedDateTime.parse(dateTime.value).run {
                weatherIcon =
                    weatherCondition.value.getWeatherIconByTimeOfDay(dayNightCalculator.calculate(toCalendar()) == DayNightCalculator.DayNight.DAY)
                time = hour.toString()
            }
        }

    }

}