package io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
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
import java.time.ZonedDateTime

class SimpleHourlyForecast(
    val items: List<Item>, val dateTimeInfo: DateTimeInfo
) : UiModel {

    val displayRainfallVolume = items.any { it.rainfallVolume.isNotEmpty() }
    val displaySnowfallVolume = items.any { it.snowfallVolume.isNotEmpty() }
    val displayPrecipitationVolume = items.any { it.precipitationVolume.isNotEmpty() }
    val displayPrecipitationProbability = items.any { it.precipitationProbability != "-" }

    companion object {
        val itemWidth: Dp = 54.dp
        val temperatureGraphHeight: Dp = 65.dp
    }

    class DateTimeInfo(val items: List<Item>, val firstItemX: Int) : UiModel {
        class Item(val beginX: Int, val date: String) {
            var endX: Int = 0
            var displayX: Int = beginX
        }
    }

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

        companion object {
            @DrawableRes val probabilityIcon = io.github.pknujsp.weatherwizard.core.common.R.drawable.ic_umbrella
            @DrawableRes val rainfallIcon = io.github.pknujsp.weatherwizard.core.common.R.drawable.raindrop
            @DrawableRes val snowfallIcon = io.github.pknujsp.weatherwizard.core.common.R.drawable.snowparticle
            val imageModifier = Modifier
                .size(16.dp)
                .padding(end = 3.dp)
        }

    }

}