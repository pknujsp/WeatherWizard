package io.github.pknujsp.weatherwizard.core.data.weather.kma

import io.github.pknujsp.weatherwizard.core.common.util.LocationDistance
import io.github.pknujsp.weatherwizard.core.data.weather.WeatherRequestPreProcessor
import io.github.pknujsp.weatherwizard.core.database.coordinate.KorCoordinateDao
import io.github.pknujsp.weatherwizard.core.model.coordinate.KorCoordinateDto
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.parameter.KmaCurrentWeatherRequestParameter
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.parameter.KmaDailyForecastRequestParameter
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.parameter.KmaHourlyForecastRequestParameter
import io.github.pknujsp.weatherwizard.core.network.datasource.kma.parameter.KmaYesterdayWeatherRequestParameter
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class KmaRequestPreProcessor @Inject constructor(
    private val korCoordinateDao: KorCoordinateDao
) : WeatherRequestPreProcessor {

    private val korAreaCodesMap = mutableMapOf<Pair<Double, Double>, KorCoordinateDto>()
    private val mutex = Mutex()

    private suspend fun findCoordinate(latitude: Double, longitude: Double): KorCoordinateDto {
        val list = korCoordinateDao.findCoordinates(latitude, longitude)
        val criteriaLatLng = doubleArrayOf(latitude, longitude)
        var minDistance = Double.MAX_VALUE
        var distance = 0.0
        val compLatLng = DoubleArray(2)
        lateinit var nearbyKmaAreaCodeDto: KorCoordinateDto

        for (dto in list) {
            compLatLng[0] = dto.latitudeSecondsDivide100.toDouble()
            compLatLng[1] = dto.longitudeSecondsDivide100.toDouble()
            distance = LocationDistance.distance(
                criteriaLatLng[0], criteriaLatLng[1], compLatLng[0], compLatLng[1],
                LocationDistance.Unit.METER
            )
            if (distance < minDistance) {
                minDistance = distance
                nearbyKmaAreaCodeDto = dto
            }
        }

        return nearbyKmaAreaCodeDto
    }

    private suspend fun getAreaCode(latitude: Double, longitude: Double): String {
        val key = Pair(latitude, longitude)
        return mutex.withLock {
            korAreaCodesMap[key]?.administrativeAreaCode
                ?: run {
                    val dto = findCoordinate(latitude, longitude)
                    mutex.withLock { korAreaCodesMap[key] = dto }
                    dto.administrativeAreaCode
                }
        }
    }

    override suspend fun getCurrentWeatherRequestParameter(
        latitude: Double,
        longitude: Double,
        requestId: Long
    ): KmaCurrentWeatherRequestParameter {
        return KmaCurrentWeatherRequestParameter(
            code = getAreaCode(latitude, longitude),
            requestId = requestId
        )
    }

    override suspend fun getHourlyForecastRequestParameter(
        latitude: Double,
        longitude: Double,
        requestId: Long
    ): KmaHourlyForecastRequestParameter {
        return KmaHourlyForecastRequestParameter(
            code = getAreaCode(latitude, longitude),
            requestId = requestId
        )
    }

    override suspend fun getDailyForecastRequestParameter(
        latitude: Double,
        longitude: Double,
        requestId: Long
    ): KmaDailyForecastRequestParameter {
        return KmaDailyForecastRequestParameter(
            code = getAreaCode(latitude, longitude),
            requestId = requestId
        )
    }

    override suspend fun getYesterdayWeatherRequestParameter(
        latitude: Double,
        longitude: Double,
        requestId: Long
    ): KmaYesterdayWeatherRequestParameter {
        return KmaYesterdayWeatherRequestParameter(
            code = getAreaCode(latitude, longitude),
            requestId = requestId
        )
    }


}