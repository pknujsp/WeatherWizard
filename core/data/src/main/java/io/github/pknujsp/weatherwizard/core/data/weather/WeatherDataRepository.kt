package io.github.pknujsp.weatherwizard.core.data.weather

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

interface WeatherDataRepository {
    suspend fun getWeatherData(
        requestWeatherData: RequestWeatherData,
        requestId: Long,
        bypassCache: Boolean = true
    ): Result<EntityModel>
}

data class RequestWeatherData(
    val majorWeatherEntityType: MajorWeatherEntityType,
    val latitude: Double,
    val longitude: Double,
    val weatherProvider: WeatherProvider,
)