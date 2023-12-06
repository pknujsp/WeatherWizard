package io.github.pknujsp.weatherwizard.core.data.weather

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

interface WeatherDataRepository {
    suspend fun getWeatherData(
        majorWeatherEntityType: MajorWeatherEntityType,
        latitude: Double,
        longitude: Double,
        weatherProvider: WeatherProvider,
        requestId: Long,
        bypassCache: Boolean = true
    ): Result<EntityModel>
}