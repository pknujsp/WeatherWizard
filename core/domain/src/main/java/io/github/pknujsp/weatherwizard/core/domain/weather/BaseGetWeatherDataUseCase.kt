package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

interface BaseGetWeatherDataUseCase<T : EntityModel> {

    suspend operator fun invoke(latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long): Result<T>
}