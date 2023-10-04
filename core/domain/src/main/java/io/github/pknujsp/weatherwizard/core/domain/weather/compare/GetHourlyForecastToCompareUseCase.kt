package io.github.pknujsp.weatherwizard.core.domain.weather.compare

import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.ToCompareHourlyForecastEntity
import javax.inject.Inject

class GetHourlyForecastToCompareUseCase @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) : BaseGetForecastToCompareUseCase<ToCompareHourlyForecastEntity> {
    override suspend fun invoke(
        latitude: Double,
        longitude: Double,
        weatherDataProviders: List<WeatherDataProvider>,
        requestId: Long
    ): Result<ToCompareHourlyForecastEntity> {
        weatherDataProviders.mapIndexed { i, provider->
            weatherDataRepository.getHourlyForecast(latitude, longitude, provider, requestId + i)
        }.let { responses ->

        }

        TODO()
    }

}