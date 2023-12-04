package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import io.github.pknujsp.weatherwizard.core.model.weather.AllWeatherDataEntity
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllWeatherDataUseCase @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) : BaseGetWeatherDataUseCase<AllWeatherDataEntity> {
    override suspend fun invoke(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ): Result<AllWeatherDataEntity> {
        return weatherDataRepository.run {
            val currentWeather = getCurrentWeather(latitude, longitude, weatherProvider, requestId)
            val hourlyForecast = getHourlyForecast(latitude, longitude, weatherProvider, requestId)
            val dailyForecast = getDailyForecast(latitude, longitude, weatherProvider, requestId)
            val yesterdayWeather = if (weatherProvider is WeatherProvider.Kma) getYesterdayWeather(latitude,
                longitude,
                weatherProvider,
                requestId) else Result.success(null)

            if (currentWeather.isSuccess and hourlyForecast.isSuccess and dailyForecast.isSuccess and yesterdayWeather.isSuccess) {
                Result.success(AllWeatherDataEntity(currentWeather.getOrThrow(),
                    hourlyForecast.getOrThrow(),
                    dailyForecast.getOrThrow(),
                    yesterdayWeather.getOrNull()))
            } else {
                Result.failure(Exception("Failed to get all weather data"))
            }
        }
    }

}