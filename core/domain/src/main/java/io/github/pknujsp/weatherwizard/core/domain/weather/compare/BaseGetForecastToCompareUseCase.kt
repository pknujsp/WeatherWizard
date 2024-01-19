package io.github.pknujsp.weatherwizard.core.domain.weather.compare

import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.weatherwizard.core.model.EntityModel

interface BaseGetForecastToCompareUseCase<T : EntityModel> {

    suspend operator fun invoke(requests: List<WeatherDataRequest.Request>): Result<T>
}