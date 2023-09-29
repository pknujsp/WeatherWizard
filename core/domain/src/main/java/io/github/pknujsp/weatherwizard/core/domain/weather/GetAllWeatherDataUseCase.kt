package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import io.github.pknujsp.weatherwizard.core.model.weather.AllWeatherDataEntity
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllWeatherDataUseCase @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) : BaseGetWeatherDataUseCase<AllWeatherDataEntity> {
    override suspend fun invoke(
        latitude: Double, longitude: Double, weatherDataProvider: WeatherDataProvider, requestId: Long
    ): Result<AllWeatherDataEntity> {
        return weatherDataRepository.run {
            val currentWeather = getCurrentWeather(latitude, longitude, weatherDataProvider, requestId)
            val hourlyForecast = getHourlyForecast(latitude, longitude, weatherDataProvider, requestId)
            val dailyForecast = getDailyForecast(latitude, longitude, weatherDataProvider, requestId)
            val yesterdayWeather = if (weatherDataProvider is WeatherDataProvider.Kma) getYesterdayWeather(latitude,
                longitude,
                weatherDataProvider,
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