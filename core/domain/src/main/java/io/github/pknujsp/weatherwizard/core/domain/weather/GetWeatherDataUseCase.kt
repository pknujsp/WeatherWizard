package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.data.aqicn.AirQualityRepository
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import javax.inject.Inject

class GetWeatherDataUseCase @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository, private val airQualityRepository: AirQualityRepository
) {

    suspend operator fun invoke(request: WeatherDataRequest.Request): WeatherResponseState {
        return request.run {
            val latitude = location.latitude
            val longitude = location.longitude

            val responseList = request.weatherDataMajorCategories.map { category ->
                category to when (category) {
                    MajorWeatherEntityType.CURRENT_CONDITION -> weatherDataRepository.getCurrentWeather(latitude,
                        longitude,
                        weatherProvider,
                        requestId,
                        false)

                    MajorWeatherEntityType.HOURLY_FORECAST -> weatherDataRepository.getHourlyForecast(latitude,
                        longitude,
                        weatherProvider,
                        requestId,
                        false)

                    MajorWeatherEntityType.DAILY_FORECAST -> weatherDataRepository.getDailyForecast(latitude,
                        longitude,
                        weatherProvider,
                        requestId,
                        false)

                    MajorWeatherEntityType.AIR_QUALITY -> airQualityRepository.getAirQuality(
                        latitude,
                        longitude,
                    )

                    MajorWeatherEntityType.YESTERDAY_WEATHER -> weatherDataRepository.getYesterdayWeather(latitude,
                        longitude,
                        weatherProvider,
                        requestId,
                        false)
                }
            }

            if (responseList.all { it.second.isSuccess }) {
                WeatherResponseState.Success(requestId,
                    location,
                    weatherProvider,
                    WeatherResponseEntity(weatherDataMajorCategories, responseList.map { it.second.getOrThrow() }))
            } else if ((weatherDataMajorCategories.size >= 2) and (responseList.filter { it.first == MajorWeatherEntityType.AIR_QUALITY }
                    .all { it.second.isFailure }) and (responseList.count { it.second.isFailure } == 1)) {
                // 대기질 응답에 오류가 발생하였더라도 대기질 단독 요청이 아니라면, 전체 요청은 성공으로 처리한다
                WeatherResponseState.PartiallySuccess(requestId,
                    location,
                    weatherProvider,
                    WeatherResponseEntity(weatherDataMajorCategories - MajorWeatherEntityType.AIR_QUALITY,
                        responseList.filter { it.second.isSuccess }.map { it.second.getOrThrow() }),
                    responseList.filter { it.second.isFailure }.map {
                        it.first
                    })
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