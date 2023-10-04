package io.github.pknujsp.weatherwizard.feature.weather.comparison.common

import androidx.lifecycle.ViewModel
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider

abstract class CompareForecastViewModel :ViewModel(){
    val weatherDataProviders = WeatherDataProvider.providers.toList()
    abstract fun load(args: RequestWeatherDataArgs)
}