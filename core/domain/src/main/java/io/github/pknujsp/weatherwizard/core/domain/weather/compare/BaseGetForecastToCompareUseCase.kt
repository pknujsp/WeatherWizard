package io.github.pknujsp.weatherwizard.core.domain.weather.compare

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider

interface BaseGetForecastToCompareUseCase<T : EntityModel> {

    suspend operator fun invoke(latitude: Double, longitude: Double, weatherDataProviders: List<WeatherDataProvider>, requestId: Long):
            Result<T>
}