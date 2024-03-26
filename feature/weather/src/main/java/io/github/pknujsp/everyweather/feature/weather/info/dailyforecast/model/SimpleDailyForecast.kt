package io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model

import androidx.compose.runtime.Stable
import io.github.pknujsp.everyweather.core.model.UiModel

@Stable
class SimpleDailyForecast(
    val items: List<Item>, val displayPrecipitationProbability: Boolean = false,
    val displayPrecipitationVolume: Boolean = false,
) : UiModel {

    data class Item(
        val id: Int,
        val date: String,
        val minTemperature: String,
        val maxTemperature: String,
        val minTemperatureInt: Int,
        val maxTemperatureInt: Int,
        val weatherConditionIcons: List<Int>,
        val weatherConditions: List<Int>,
        val precipitationProbabilities: List<String>,
        val precipitationVolume: String = "",
    )
}