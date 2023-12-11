package io.github.pknujsp.weatherwizard.core.data.weather

import io.github.pknujsp.weatherwizard.core.data.weather.model.WeatherModel
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
    ): Result<WeatherModel>
}

class RequestWeatherData(
    val majorWeatherEntityTypes: Set<MajorWeatherEntityType>,
    val latitude: Double,
    val longitude: Double,
    val weatherProvider: WeatherProvider,
) {
    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + weatherProvider.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RequestWeatherData

        if (majorWeatherEntityTypes != other.majorWeatherEntityTypes) return false
        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false
        return weatherProvider == other.weatherProvider
    }
}