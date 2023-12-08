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
        request: WeatherDataRequest.Request, bypassCache: Boolean = true
    ): WeatherResponseState {
        return request.run {
            val responseList = supervisorScope {
                request.weatherDataMajorCategories.map {
                    async(dispatcher) {
                        it to weatherDataRepository.getWeatherData(RequestWeatherData(it,
                            location.latitude,
                            location.longitude,
                            weatherProvider), requestId, bypassCache)
                    }
                }
            }

            if (responseList.all { it.await().second.isSuccess }) {
                WeatherResponseState.Success(requestId,
                    location,
                    weatherProvider,
                    WeatherResponseEntity(weatherDataMajorCategories, responseList.map { it.await().second.getOrNull()!! }))
            } else {
                WeatherResponseState.Failure(
                    requestId,
                    location,
                    weatherProvider,
                )
            }

        }

    }

}