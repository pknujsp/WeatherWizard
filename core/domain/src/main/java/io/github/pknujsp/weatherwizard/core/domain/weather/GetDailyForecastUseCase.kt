package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDailyForecastUseCase @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) : BaseGetWeatherDataUseCase<DailyForecastEntity> {
    override suspend fun invoke(
        latitude: Double,
        longitude: Double,
        weatherProvider: WeatherProvider,
        requestId: Long
    ): Result<DailyForecastEntity> {
        return weatherDataRepository.getDailyForecast(latitude, longitude, weatherProvider, requestId)
    }

}