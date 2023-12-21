package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.data.weather.RequestWeatherData
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import io.github.pknujsp.weatherwizard.core.data.weather.model.WeatherModel
import javax.inject.Inject

class GetWeatherDataUseCase @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository,
) {

    private suspend fun load(
        request: WeatherDataRequest.Request, bypassCache: Boolean
    ): Result<WeatherModel> {
        val requestWeatherData = request.run {
            RequestWeatherData(weatherDataMajorCategories, location.latitude, location.longitude, weatherProvider)
        }
        return weatherDataRepository.getWeatherData(requestWeatherData, request.requestId, bypassCache)
    }

    suspend operator fun invoke(
        request: WeatherDataRequest.Request, bypassCache: Boolean = false
    ): WeatherResponseState {
        return request.run {
            load(this, bypassCache).fold(onSuccess = {
                WeatherResponseState.Success(requestId,
                    location,
                    weatherProvider,
                    WeatherResponseEntity(weatherDataMajorCategories, it.list, DayNightCalculator(location.latitude, location.longitude)))
            }, onFailure = {
                WeatherResponseState.Failure(
                    requestId,
                    location,
                    weatherProvider,
                )
            })
        }
    }

}