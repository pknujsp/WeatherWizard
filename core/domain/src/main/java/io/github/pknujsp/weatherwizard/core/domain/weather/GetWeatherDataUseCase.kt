package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.data.weather.RequestWeatherData
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

class GetWeatherDataUseCase @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository,
    @CoDispatcher(CoDispatcherType.MULTIPLE) private val dispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(
        request: WeatherDataRequest.Request, bypassCache: Boolean = false
    ): WeatherResponseState {
        return request.run {
            val response = supervisorScope {
                async(dispatcher) {
                    weatherDataRepository.getWeatherData(RequestWeatherData(request.weatherDataMajorCategories,
                        location.latitude,
                        location.longitude,
                        weatherProvider), requestId, bypassCache)
                }
            }

            response.await().fold(onSuccess = {
                WeatherResponseState.Success(requestId,
                    location,
                    weatherProvider,
                    WeatherResponseEntity(weatherDataMajorCategories, it.list))
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