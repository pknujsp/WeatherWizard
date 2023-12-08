package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.data.weather.RequestWeatherData
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import javax.inject.Inject

class GetWeatherDataUseCase @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) {

    suspend operator fun invoke(request: WeatherDataRequest.Request, bypassCache: Boolean = true): WeatherResponseState {
        return request.run {
            val latitude = location.latitude
            val longitude = location.longitude

            val responseList = request.weatherDataMajorCategories.map {
                it to weatherDataRepository.getWeatherData(RequestWeatherData(it, latitude, longitude, weatherProvider),
                    requestId,
                    bypassCache)
            }

            if (responseList.all { it.second.isSuccess }) {
                WeatherResponseState.Success(requestId,
                    location,
                    weatherProvider,
                    WeatherResponseEntity(weatherDataMajorCategories, responseList.map { it.second.getOrNull()!! }))
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