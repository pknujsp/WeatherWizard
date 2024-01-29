package io.github.pknujsp.everyweather.core.domain.weather

import io.github.pknujsp.everyweather.core.common.util.DayNightCalculator
import io.github.pknujsp.everyweather.core.data.weather.RequestWeatherData
import io.github.pknujsp.everyweather.core.data.weather.WeatherDataRepository
import io.github.pknujsp.everyweather.core.data.weather.model.WeatherModel
import javax.inject.Inject

class GetWeatherDataUseCase @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository,
) {

    private suspend fun load(
        request: WeatherDataRequest.Request, bypassCache: Boolean
    ): Result<WeatherModel> {
        val requestWeatherData = request.run {
            RequestWeatherData(categories, coordinate.latitude, coordinate.longitude, weatherProvider)
        }
        return try {
            weatherDataRepository.getWeatherData(requestWeatherData, request.requestId, bypassCache)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend operator fun invoke(
        request: WeatherDataRequest.Request, bypassCache: Boolean = false
    ): WeatherResponseState {
        return request.run {
            load(this, bypassCache).fold(onSuccess = {
                WeatherResponseState.Success(requestId,
                    coordinate,
                    weatherProvider,
                    WeatherResponseEntity(categories, it.list, DayNightCalculator(coordinate.latitude, coordinate.longitude)))
            }, onFailure = {
                WeatherResponseState.Failure(
                    requestId,
                    coordinate,
                    weatherProvider,
                )
            })
        }
    }

}