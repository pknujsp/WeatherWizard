package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.data.aqicn.AirQualityRepository
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataMajorCategory
import javax.inject.Inject

class GetWeatherDataUseCase @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository, private val airQualityRepository: AirQualityRepository
) {

    suspend operator fun invoke(request: RequestEntity.Request): ResponseState {
        return request.run {
            val latitude = coordinate.latitude
            val longitude = coordinate.longitude

            val responseList = request.weatherDataMajorCategories.map { category ->
                category to when (category) {
                    WeatherDataMajorCategory.CURRENT_CONDITION -> weatherDataRepository.getCurrentWeather(latitude,
                        longitude,
                        weatherDataProvider,
                        requestId)

                    WeatherDataMajorCategory.HOURLY_FORECAST -> weatherDataRepository.getHourlyForecast(latitude,
                        longitude,
                        weatherDataProvider,
                        requestId)

                    WeatherDataMajorCategory.DAILY_FORECAST -> weatherDataRepository.getDailyForecast(latitude,
                        longitude,
                        weatherDataProvider,
                        requestId)

                    WeatherDataMajorCategory.AIR_QUALITY -> airQualityRepository.getAirQuality(
                        latitude,
                        longitude,
                    )

                    WeatherDataMajorCategory.YESTERDAY_WEATHER -> weatherDataRepository.getYesterdayWeather(latitude,
                        longitude,
                        weatherDataProvider,
                        requestId)
                }
            }

            if (responseList.all { it.second.isSuccess }) {
                ResponseState.Success(ResponseEntity(requestId,
                    coordinate,
                    weatherDataProvider,
                    weatherDataMajorCategories,
                    responseList.map { it.second.getOrThrow() }))
            } else if ((weatherDataMajorCategories.size >= 2) and (responseList.filter { it.first == WeatherDataMajorCategory.AIR_QUALITY }
                    .all { it.second.isFailure }) and (responseList.count { it.second.isFailure } == 1)) {
                // 대기질 응답에 오류가 발생하였더라도 대기질 단독 요청이 아니라면, 전체 요청은 성공으로 처리한다
                ResponseState.PartiallySuccess(ResponseEntity(requestId,
                    coordinate,
                    weatherDataProvider,
                    weatherDataMajorCategories - WeatherDataMajorCategory.AIR_QUALITY,
                    responseList.filter { it.second.isSuccess }.map { it.second.getOrThrow() }),
                    responseList.filter { it.second.isFailure }.map {
                        it.first
                    })
            } else {
                ResponseState.Failure
            }

        }

    }
}