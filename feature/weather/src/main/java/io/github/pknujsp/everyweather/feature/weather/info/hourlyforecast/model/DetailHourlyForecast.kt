package io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model

import androidx.annotation.StringRes
import io.github.pknujsp.everyweather.core.model.UiModel

class DetailHourlyForecast(
    val items: List<Pair<Header, List<Item>>>,
    val displayRainfallVolume: Boolean,
    val displaySnowfallVolume: Boolean,
    val displayPrecipitationVolume: Boolean,
    val displayPrecipitationProbability: Boolean
) {
    class Item(
        val id: Int,
        val hour: String,
        val temperature: String,
        val precipitationProbability: String,
        val precipitationVolume: String,
        val rainfallVolume: String,
        val snowfallVolume: String,
        val weatherIcon: Int,
        @StringRes val weatherCondition: Int,
    ) : UiModel

    class Header(
        val id: Int,
        val title: String,
    ) : UiModel
}