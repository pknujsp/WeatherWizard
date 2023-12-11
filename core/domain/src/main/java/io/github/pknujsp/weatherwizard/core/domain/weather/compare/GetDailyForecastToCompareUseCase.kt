package io.github.pknujsp.weatherwizard.core.domain.weather.compare

import io.github.pknujsp.weatherwizard.core.data.weather.RequestWeatherData
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherDataRepository
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.ToCompareDailyForecastEntity
import javax.inject.Inject

class GetDailyForecastToCompareUseCase @Inject constructor(
    private val weatherDataRepository: WeatherDataRepository
) : BaseGetForecastToCompareUseCase<ToCompareDailyForecastEntity> {
    override suspend fun invoke(
        requests: List<WeatherDataRequest.Request>
    ): Result<ToCompareDailyForecastEntity> {
        return requests.map { request ->
            request.weatherProvider to weatherDataRepository.getWeatherData(RequestWeatherData(latitude = request.location.latitude,
                longitude = request.location.longitude,
                weatherProvider = request.weatherProvider,
                majorWeatherEntityTypes = request.weatherDataMajorCategories), request.requestId, false)
        }.let { responses ->
            if (responses.all { it.second.isSuccess }) {
                Result.success(ToCompareDailyForecastEntity(responses.map {
                    it.first to (it.second.getOrThrow().list.first() as DailyForecastEntity)
                }))
            } else {
                Result.failure(Throwable())
            }
        }
    }

}