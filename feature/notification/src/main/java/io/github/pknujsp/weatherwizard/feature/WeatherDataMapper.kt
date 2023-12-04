package io.github.pknujsp.weatherwizard.feature

import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.UiModel

interface WeatherDataMapper {
    fun map(response: WeatherResponseState): MapperResult

    sealed interface MapperResult {
        data class Success(val uiModel: UiModel) : MapperResult
        data object Failure : MapperResult
    }
}