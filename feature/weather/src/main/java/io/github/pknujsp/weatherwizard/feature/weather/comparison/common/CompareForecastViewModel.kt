package io.github.pknujsp.weatherwizard.feature.weather.comparison.common

import androidx.lifecycle.ViewModel
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

abstract class CompareForecastViewModel :ViewModel(){
    val weatherProviders = WeatherProvider.enums.toList()
    abstract fun load(args: RequestWeatherArguments)
}