package io.github.pknujsp.everyweather.core.domain.weather.compare

import io.github.pknujsp.everyweather.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.everyweather.core.model.EntityModel

interface BaseGetForecastToCompareUseCase<T : EntityModel> {
    suspend operator fun invoke(requests: List<WeatherDataRequest.Request>): Result<T>
}
