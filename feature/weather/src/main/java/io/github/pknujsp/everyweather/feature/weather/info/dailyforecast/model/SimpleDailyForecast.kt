package io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.pknujsp.everyweather.core.model.UiModel
import io.github.pknujsp.everyweather.core.model.settings.CurrentUnits
import io.github.pknujsp.everyweather.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.everyweather.core.model.weather.dailyforecast.DailyForecastEntity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Stable
class SimpleDailyForecast(
    val items: List<Item>, val displayPrecipitationProbability: Boolean
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
    )
}