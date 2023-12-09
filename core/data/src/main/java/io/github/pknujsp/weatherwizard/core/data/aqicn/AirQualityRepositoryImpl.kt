package io.github.pknujsp.weatherwizard.core.data.aqicn

import io.github.pknujsp.weatherwizard.core.data.RepositoryCacheManager
import io.github.pknujsp.weatherwizard.core.data.cache.CacheManager
import io.github.pknujsp.weatherwizard.core.data.cache.CacheManagerImpl
import io.github.pknujsp.weatherwizard.core.model.VarState
import io.github.pknujsp.weatherwizard.core.model.airquality.AirQualityDescription
import io.github.pknujsp.weatherwizard.core.model.airquality.AirQualityEntity
import io.github.pknujsp.weatherwizard.core.model.weather.common.AirQualityValueType
import io.github.pknujsp.weatherwizard.core.network.api.aqicn.AqiCnDataSource
import io.github.pknujsp.weatherwizard.core.network.api.aqicn.AqiCnResponse
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Inject

class AirQualityRepositoryImpl @Inject constructor(
    private val aqiCnDataSource: AqiCnDataSource, cacheManager: CacheManager<AirQualityEntity>
) : AirQualityRepository, RepositoryCacheManager<AirQualityEntity>(cacheManager) {
    override suspend fun getAirQuality(latitude: Double, longitude: Double): Result<AirQualityEntity> {
        val key = toKey(latitude, longitude)
        getCache(key)?.run {
            return Result.success(this)
        }

        val result = aqiCnDataSource.getAqiCnData(latitude, longitude).map { response ->
            response.data.run {
                val current = AirQualityEntity.Current(aqi = AirQualityValueType(value = aqi.toInt(),
                    airQualityDescription = AirQualityDescription.fromValue(aqi.toInt())),
                    co = AirQualityValueType(value = iaqi.co.v.toInt(),
                        airQualityDescription = AirQualityDescription.fromValue(iaqi.co.v.toInt())),
                    no2 = AirQualityValueType(value = iaqi.no2.v.toInt(),
                        airQualityDescription = AirQualityDescription.fromValue(iaqi.no2.v.toInt())),
                    o3 = AirQualityValueType(value = iaqi.o3.v.toInt(),
                        airQualityDescription = AirQualityDescription.fromValue(iaqi.o3.v.toInt())),
                    pm10 = AirQualityValueType(value = iaqi.pm10.v.toInt(),
                        airQualityDescription = AirQualityDescription.fromValue(iaqi.pm10.v.toInt())),
                    pm25 = AirQualityValueType(value = iaqi.pm25.v.toInt(),
                        airQualityDescription = AirQualityDescription.fromValue(iaqi.pm25.v.toInt())),
                    so2 = AirQualityValueType(value = iaqi.so2.v.toInt(),
                        airQualityDescription = AirQualityDescription.fromValue(iaqi.so2.v.toInt())))

                val info = AirQualityEntity.Info(dataMeasurementTime = ZonedDateTime.parse(time.iso),
                    dataSourceName = attributions.first().name,
                    dataSourceWebsiteUrl = attributions.first().url,
                    stationLatitude = city.geo[0].toDouble(),
                    stationLongitude = city.geo[1].toDouble(),
                    stationName = city.name)

                val items: List<AirQualityEntity.DailyForecast.Item> = forecast.daily.run {
                    val o3 = o3.map()
                    val pm10 = pm10.map()
                    val pm25 = pm25.map()

                    (o3.keys + pm10.keys + pm25.keys).toSortedSet { d1, d2 ->
                        d1.compareTo(d2)
                    }.map { date ->
                        AirQualityEntity.DailyForecast.Item(date = date,
                            o3 = o3[date]?.run { VarState.Initialized(this) } ?: VarState.Uninitialized,
                            pm10 = pm10[date]?.run { VarState.Initialized(this) } ?: VarState.Uninitialized,
                            pm25 = pm25[date]?.run { VarState.Initialized(this) } ?: VarState.Uninitialized)
                    }
                }

                val dailyForecast = AirQualityEntity.DailyForecast(items = items)
                AirQualityEntity(current = current, info = info, dailyForecast = dailyForecast)
            }
        }

        if (result.isSuccess) {
            cacheManager.put(key, result.getOrThrow())
        }
        return result
    }

    private fun String.toInt(): Int = toIntOrNull() ?: toDoubleOrNull()?.toInt() ?: 0

    private fun List<AqiCnResponse.Data.Forecast.Daily.ForecastPollutant>.map() = associate {
        LocalDate.parse(it.day) to AirQualityEntity.DailyForecast.Item.Pollutant(avg = AirQualityValueType(value = it.avg.toInt(),
            airQualityDescription = AirQualityDescription.fromValue(it.avg.toInt())),
            max = AirQualityValueType(value = it.max.toInt(), airQualityDescription = AirQualityDescription.fromValue(it.max.toInt())),
            min = AirQualityValueType(value = it.min.toInt(), airQualityDescription = AirQualityDescription.fromValue(it.min.toInt())))
    }

    private suspend fun getCache(
        key: String
    ): AirQualityEntity? = when (val cacheState = cacheManager.get<AirQualityEntity>(key)) {
        is CacheManager.CacheState.Hit -> {
            cacheState.value
        }

        else -> null
    }

    private fun toKey(latitude: Double, longitude: Double): String = "$latitude-$longitude"

}