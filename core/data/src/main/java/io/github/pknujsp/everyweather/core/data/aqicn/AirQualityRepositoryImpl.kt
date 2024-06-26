package io.github.pknujsp.everyweather.core.data.aqicn

import io.github.pknujsp.everyweather.core.data.RepositoryCacheManager
import io.github.pknujsp.everyweather.core.data.cache.CacheCleaner
import io.github.pknujsp.everyweather.core.data.cache.CacheManager
import io.github.pknujsp.everyweather.core.model.VarState
import io.github.pknujsp.everyweather.core.model.airquality.AirQualityDescription
import io.github.pknujsp.everyweather.core.model.airquality.AirQualityEntity
import io.github.pknujsp.everyweather.core.model.weather.common.AirQualityValueType
import io.github.pknujsp.everyweather.core.network.api.aqicn.AqiCnDataSource
import io.github.pknujsp.everyweather.core.network.api.aqicn.AqiCnResponse
import java.time.LocalDate
import java.time.ZonedDateTime

internal class AirQualityRepositoryImpl(
    private val aqiCnDataSource: AqiCnDataSource,
    cacheManager: CacheManager<Int, AirQualityEntity>,
    cacheCleaner: CacheCleaner,
) : AirQualityRepository, RepositoryCacheManager<Int, AirQualityEntity>(cacheCleaner, cacheManager) {
    private suspend fun load(
        latitude: Double,
        longitude: Double,
    ): Result<AqiCnResponse> = aqiCnDataSource.getAqiCnData(latitude, longitude)

    override suspend fun getAirQuality(
        latitude: Double,
        longitude: Double,
    ): Result<AirQualityEntity> {
        val key = toKey(latitude, longitude)
        getCache(key)?.run {
            return Result.success(this)
        }

        val result = load(latitude, longitude).map { response ->
            response.data.run {
                val current = AirQualityEntity.Current(
                    aqi = AirQualityValueType(
                        value = aqi.toInt().toShort(),
                        airQualityDescription = AirQualityDescription.fromValue(aqi.toInt().toShort()),
                    ),
                    co = AirQualityValueType(
                        value = iaqi.co.v.toInt().toShort(),
                        airQualityDescription = AirQualityDescription.fromValue(iaqi.co.v.toInt().toShort()),
                    ),
                    no2 = AirQualityValueType(
                        value = iaqi.no2.v.toInt().toShort(),
                        airQualityDescription = AirQualityDescription.fromValue(iaqi.no2.v.toInt().toShort()),
                    ),
                    o3 = AirQualityValueType(
                        value = iaqi.o3.v.toInt().toShort(),
                        airQualityDescription = AirQualityDescription.fromValue(iaqi.o3.v.toInt().toShort()),
                    ),
                    pm10 = AirQualityValueType(
                        value = iaqi.pm10.v.toInt().toShort(),
                        airQualityDescription = AirQualityDescription.fromValue(iaqi.pm10.v.toInt().toShort()),
                    ),
                    pm25 = AirQualityValueType(
                        value = iaqi.pm25.v.toInt().toShort(),
                        airQualityDescription = AirQualityDescription.fromValue(iaqi.pm25.v.toInt().toShort()),
                    ),
                    so2 = AirQualityValueType(
                        value = iaqi.so2.v.toInt().toShort(),
                        airQualityDescription = AirQualityDescription.fromValue(iaqi.so2.v.toInt().toShort()),
                    ),
                )

                val info = AirQualityEntity.Info(
                    dataMeasurementTime = ZonedDateTime.parse(time.iso).toString(),
                    dataSourceName = attributions.first().name,
                    dataSourceWebsiteUrl = attributions.first().url,
                    stationLatitude = city.geo[0].toDouble(),
                    stationLongitude = city.geo[1].toDouble(),
                    stationName = city.name,
                )

                val items: List<AirQualityEntity.DailyForecast.Item> = forecast.daily.run {
                    val o3 = o3.map()
                    val pm10 = pm10.map()
                    val pm25 = pm25.map()

                    (o3.keys + pm10.keys + pm25.keys).toSortedSet { d1, d2 ->
                        d1.compareTo(d2)
                    }.map { date ->
                        AirQualityEntity.DailyForecast.Item(
                            date = date.toString(),
                            o3 = o3[date]?.run { VarState.Initialized(this) } ?: VarState.Uninitialized,
                            pm10 = pm10[date]?.run { VarState.Initialized(this) } ?: VarState.Uninitialized,
                            pm25 = pm25[date]?.run { VarState.Initialized(this) } ?: VarState.Uninitialized,
                        )
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
        LocalDate.parse(it.day) to AirQualityEntity.DailyForecast.Item.Pollutant(
            avg = AirQualityValueType(
                value = it.avg.toInt().toShort(),
                airQualityDescription = AirQualityDescription.fromValue(it.avg.toInt().toShort()),
            ),
            max = AirQualityValueType(value = it.max.toInt().toShort(),
                airQualityDescription = AirQualityDescription.fromValue(it.max.toInt().toShort())),
            min = AirQualityValueType(value = it.min.toInt().toShort(),
                airQualityDescription = AirQualityDescription.fromValue(it.min.toInt().toShort())),
        )
    }

    private suspend fun getCache(key: Int): AirQualityEntity? = when (val cacheState = cacheManager.get(key)) {
        is CacheManager.CacheState.Hit -> cacheState.value
        else -> null
    }

    private fun toKey(
        latitude: Double,
        longitude: Double,
    ) = latitude.hashCode() + 31 * longitude.hashCode()
}