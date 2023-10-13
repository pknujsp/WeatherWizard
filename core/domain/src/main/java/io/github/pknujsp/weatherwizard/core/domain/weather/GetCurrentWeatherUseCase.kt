package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCurrentWeatherUseCase @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) : BaseGetWeatherDataUseCase<CurrentWeatherEntity> {
    override suspend fun invoke(
        latitude: Double,
        longitude: Double,
        weatherDataProvider: WeatherDataProvider,
        requestId: Long
    ): Result<CurrentWeatherEntity> {
        return weatherDataRepository.getCurrentWeather(latitude, longitude, weatherDataProvider, requestId)
    }

}