package io.github.pknujsp.weatherwizard.core.data.weather

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider

interface WeatherDataRepository {

    /**
     * 날씨 데이터를 가져온다.
     * @param requestWeatherData 날씨 데이터를 가져올 때 필요한 정보
     * @param requestId 요청 ID
     * @param bypassCache 캐시를 우회할지 여부
     * @return 날씨 데이터
     */
    suspend fun getWeatherData(
        requestWeatherData: RequestWeatherData, requestId: Long, bypassCache: Boolean = true
    ): Result<EntityModel>
}


data class RequestWeatherData(
    val majorWeatherEntityType: MajorWeatherEntityType,
    val latitude: Double,
    val longitude: Double,
    val weatherProvider: WeatherProvider,
)