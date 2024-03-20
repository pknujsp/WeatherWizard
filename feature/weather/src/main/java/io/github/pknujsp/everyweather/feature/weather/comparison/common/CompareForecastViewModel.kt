package io.github.pknujsp.everyweather.feature.weather.comparison.common

import androidx.lifecycle.ViewModel
import io.github.pknujsp.everyweather.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider

abstract class CompareForecastViewModel : ViewModel() {
    val weatherProviders get() = WeatherProvider.enums

    abstract fun load(args: RequestWeatherArguments)
}
