package io.github.pknujsp.weatherwizard.core.data.weather.kma

import io.github.pknujsp.weatherwizard.core.common.util.GeographicalDistanceCalculator
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherRequestPreProcessor
import io.github.pknujsp.weatherwizard.core.database.kma.KmaAreaCodesDao
import io.github.pknujsp.weatherwizard.core.model.ApiRequestParameter
import io.github.pknujsp.weatherwizard.core.model.coordinate.KmaAreaCodesDto
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaCurrentWeatherRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaDailyForecastRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaHourlyForecastRequestParameter
import io.github.pknujsp.weatherwizard.core.model.weather.kma.parameter.KmaYesterdayWeatherRequestParameter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class KmaRequestPreProcessor(
    private val kmaAreaCodesDao: KmaAreaCodesDao
) : WeatherRequestPreProcessor {

    private val areaCodesMap = mutableMapOf<Pair<Double, Double>, StateFlow<QueryState>>()

    private suspend fun findClosestArea(latitude: Double, longitude: Double): KmaAreaCodesDto {
        val list = kmaAreaCodesDao.findCoordinates(latitude, longitude)
        var minDistance = Double.MAX_VALUE
        var distance: Double
        var closestArea: KmaAreaCodesDto = list.first()

        for (dto in list) {
            distance = GeographicalDistanceCalculator.calculateDistance(latitude, longitude, dto.latitude, dto.longitude)
            if (distance < minDistance) {
                minDistance = distance
                closestArea = dto
            }
        }

        return closestArea
    }

    private fun getAreaCode(latitude: Double, longitude: Double) = channelFlow {
        val key = latitude to longitude

        if (key !in areaCodesMap) {
            val flow: MutableStateFlow<QueryState> = MutableStateFlow(QueryState.Loading)
            areaCodesMap[key] = flow.asStateFlow()

            flow.run {
                try {
                    val area = findClosestArea(latitude, longitude)
                    value = QueryState.Loaded(area.districtCode)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        areaCodesMap[key]?.filter { it is QueryState.Loaded }?.map { it as QueryState.Loaded }?.first()?.let {
            trySend(it.code)
        }
    }

    override suspend fun getCurrentWeatherRequestParameter(
        latitude: Double, longitude: Double, requestId: Long
    ): ApiRequestParameter {
        return KmaCurrentWeatherRequestParameter(code = getAreaCode(latitude, longitude).first(), requestId = requestId)
    }

    override suspend fun getHourlyForecastRequestParameter(
        latitude: Double, longitude: Double, requestId: Long
    ): ApiRequestParameter {
        return KmaHourlyForecastRequestParameter(code = getAreaCode(latitude, longitude).first(), requestId = requestId)
    }

    override suspend fun getDailyForecastRequestParameter(
        latitude: Double, longitude: Double, requestId: Long
    ): ApiRequestParameter {
        return KmaDailyForecastRequestParameter(code = getAreaCode(latitude, longitude).first(), requestId = requestId)
    }

    override suspend fun getYesterdayWeatherRequestParameter(
        latitude: Double, longitude: Double, requestId: Long
    ): ApiRequestParameter {
        return KmaYesterdayWeatherRequestParameter(code = getAreaCode(latitude, longitude).first(), requestId = requestId)
    }

    private sealed interface QueryState {
        data object Loading : QueryState
        data class Loaded(val code: String) : QueryState
    }

}