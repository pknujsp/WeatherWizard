package io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.pknujsp.everyweather.core.model.UiModel
import io.github.pknujsp.everyweather.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.everyweather.core.model.weather.common.ProbabilityValueType
import io.github.pknujsp.everyweather.core.model.weather.common.RainfallValueType
import io.github.pknujsp.everyweather.core.model.weather.common.SnowfallValueType
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherConditionValueType

class CompareHourlyForecast(
    val items: List<Item>,
) : UiModel {
    val displayRainfallVolume = items.any { it.rainfallVolume.isNotEmpty() }
    val displaySnowfallVolume = items.any { it.snowfallVolume.isNotEmpty() }
    val displayPrecipitationVolume = items.any { it.precipitationVolume.isNotEmpty() }
    val displayPrecipitationProbability = items.any { it.precipitationProbability != "-" }

    class Item(
        val id: Int,
        weatherCondition: WeatherConditionValueType,
        val hour: String,
        temperature: TemperatureValueType,
        rainfallVolume: RainfallValueType = RainfallValueType.None,
        snowfallVolume: SnowfallValueType = SnowfallValueType.None,
        rainfallProbability: ProbabilityValueType = ProbabilityValueType.None,
        snowfallProbability: ProbabilityValueType = ProbabilityValueType.None,
        precipitationVolume: PrecipitationValueType,
        precipitationProbability: ProbabilityValueType,
        isDay: Boolean,
    ) : UiModel {
        val temperature: String = temperature.toString()
        val precipitationProbability: String = precipitationProbability.toString()
        val precipitationVolume: String = precipitationVolume.toStringWithoutUnit()
        val rainfallVolume: String = rainfallVolume.toStringWithoutUnit()
        val snowfallVolume: String = snowfallVolume.toStringWithoutUnit()

        @DrawableRes val weatherIcon: Int = weatherCondition.value.getWeatherIconByTimeOfDay(isDay)

        @StringRes val weatherCondition: Int = weatherCondition.value.stringRes
    }
}