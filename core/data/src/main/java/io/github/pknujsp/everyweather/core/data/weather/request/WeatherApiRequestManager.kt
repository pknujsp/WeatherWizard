package io.github.pknujsp.everyweather.core.data.weather.request

import io.github.pknujsp.everyweather.core.model.ApiResponseModel
import io.github.pknujsp.everyweather.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherProvider

@JvmSuppressWildcards
interface WeatherApiRequestManager<T : ApiResponseModel> {
    suspend fun get(
        latitude: Double,
        longitude: Double,
        weatherProvider: WeatherProvider,
        majorWeatherEntityType: MajorWeatherEntityType,
        requestId: Long,
    ): Result<T>
}
