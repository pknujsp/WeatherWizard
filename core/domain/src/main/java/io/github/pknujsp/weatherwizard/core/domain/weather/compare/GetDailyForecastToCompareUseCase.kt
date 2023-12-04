package io.github.pknujsp.weatherwizard.core.domain.weather.compare

import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.ToCompareDailyForecastEntity
import javax.inject.Inject

class GetDailyForecastToCompareUseCase @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) : BaseGetForecastToCompareUseCase<ToCompareDailyForecastEntity> {
    override suspend fun invoke(
        latitude: Double,
        longitude: Double,
        weatherProviders: List<WeatherProvider>,
        requestId: Long
    ): Result<ToCompareDailyForecastEntity> {
        return weatherProviders.mapIndexed { i, provider ->
            weatherDataRepository.getDailyForecast(latitude, longitude, provider, requestId + i)
        }.let { responses ->
            val success = responses.all { it.isSuccess }
            if (success) {
                Result.success(ToCompareDailyForecastEntity(
                    weatherProviders.zip(responses.map { it.getOrThrow() }) { provider, entity ->
                        provider to entity
                    }.toList()
                ))
            } else {
                Result.failure(responses.first { it.isFailure }.exceptionOrNull()!!)
            }
        }
    }


}