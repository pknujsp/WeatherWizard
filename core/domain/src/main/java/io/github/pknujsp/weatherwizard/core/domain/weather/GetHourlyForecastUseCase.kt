package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetHourlyForecastUseCase @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) : BaseGetWeatherDataUseCase<HourlyForecastEntity> {
    override suspend fun invoke(
        latitude: Double,
        longitude: Double,
        weatherProvider: WeatherProvider,
        requestId: Long
    ): Result<HourlyForecastEntity> {
        return weatherDataRepository.getHourlyForecast(latitude, longitude, weatherProvider, requestId, false)
    }

}