package io.github.pknujsp.weatherwizard.feature.weather.comparison.common

import androidx.lifecycle.ViewModel
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs

abstract class CompareForecastViewModel :ViewModel(){
    abstract fun load(args: RequestWeatherDataArgs)
}