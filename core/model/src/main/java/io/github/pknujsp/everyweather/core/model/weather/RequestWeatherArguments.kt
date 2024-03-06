package io.github.pknujsp.everyweather.core.model.weather

import androidx.compose.runtime.Stable
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider

@Stable
data class RequestWeatherArguments(
    val weatherProvider: WeatherProvider,
    val targetLocation: TargetLocationModel,
) {
    val key = weatherProvider.hashCode() + targetLocation.hashCode() / 31
}