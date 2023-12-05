package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton

class GetYesterdayWeatherUseCase  @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) : BaseGetWeatherDataUseCase<YesterdayWeatherEntity> {
    override suspend fun invoke(latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long): Result<YesterdayWeatherEntity> {
        TODO("Not yet implemented")
    }

}