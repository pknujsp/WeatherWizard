package io.github.pknujsp.weatherwizard.core.domain.weather.compare

import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequestBuilder
import io.github.pknujsp.weatherwizard.core.model.EntityModel

interface BaseGetForecastToCompareUseCase<T : EntityModel> {

    suspend operator fun invoke(requests: List<WeatherDataRequestBuilder.Request>): Result<T>
}