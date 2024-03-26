package io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import io.github.pknujsp.everyweather.core.model.UiModel
import io.github.pknujsp.everyweather.core.ui.time.DateTimeInfo
import java.time.ZonedDateTime

class SimpleHourlyForecast(
    val items: List<Item>,
    val times:List<ZonedDateTime>,
    val displayRainfallVolume: Boolean,
    val displaySnowfallVolume: Boolean,
    val displayPrecipitationVolume: Boolean,
    val displayPrecipitationProbability: Boolean
) {
    data class Item(
        val id: Int,
        val time: String,
        val temperature: String,
        val temperatureInt: Int,
        val precipitationProbability: String,
        val precipitationVolume: String,
        val rainfallVolume: String,
        val snowfallVolume: String,
        val weatherIcon: Int,
        @StringRes val weatherCondition: Int,
    ) : UiModel
}