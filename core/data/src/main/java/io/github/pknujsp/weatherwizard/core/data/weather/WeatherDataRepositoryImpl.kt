package io.github.pknujsp.weatherwizard.core.data.weather

import androidx.compose.ui.platform.isDebugInspectorInfoEnabled
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapperManager
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestManager
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.time.toKotlinDuration

class WeatherDataRepositoryImpl @Inject constructor(
    private val weatherResponseMapperManager: WeatherResponseMapperManager,
    private val weatherApiRequestManager: WeatherApiRequestManager,
) : WeatherDataRepository, WeatherDataRepositoryInitializer {

    private val cacheManager = CacheManager()

    private inline fun <reified T : EntityModel> getCache(
        key: String
    ): Result<T>? = when (val cacheState = cacheManager.get(key)) {
        is CacheManager.CacheState.Valid -> {
            val foundCache = cacheState.list.firstOrNull { it.getOrThrow() is T }
            foundCache?.run {
                if (isSuccess) {
                    Result.success(getOrThrow() as T)
                } else {
                    null
                }
            }
        }

        else -> null
    }


    override suspend fun getCurrentWeather(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long, bypassCache: Boolean
    ): Result<CurrentWeatherEntity> {
        val key = toKey(latitude, longitude, weatherProvider)
        if (!bypassCache) {
            val cache = getCache<CurrentWeatherEntity>(key)
            if (cache != null) {
                return cache
            }
        }

        val result =
            weatherApiRequestManager.getCurrentWeather(latitude, longitude, weatherProvider, requestId).fold(onSuccess = { response ->
                Result.success(weatherResponseMapperManager.mapCurrentWeather(response, weatherProvider))
            }, onFailure = { error ->
                Result.failure(error)
            })

        if (!bypassCache) {
            cacheManager.put(key, result)
        }
        return result
    }

    override suspend fun getHourlyForecast(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long, bypassCache: Boolean
    ): Result<HourlyForecastEntity> {
        val key = toKey(latitude, longitude, weatherProvider)
        if (!bypassCache) {
            val cache = getCache<HourlyForecastEntity>(key)
            if (cache != null) {
                return cache
            }
        }
        val result =
            weatherApiRequestManager.getHourlyForecast(latitude, longitude, weatherProvider, requestId).fold(onSuccess = { response ->
                Result.success(weatherResponseMapperManager.mapHourlyForecast(response, weatherProvider))
            }, onFailure = { error ->
                Result.failure(error)
            })
        if (!bypassCache) {
            cacheManager.put(key, result)
        }
        return result
    }

    override suspend fun getDailyForecast(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long, bypassCache: Boolean
    ): Result<DailyForecastEntity> {
        val key = toKey(latitude, longitude, weatherProvider)
        if (!bypassCache) {
            val cache = getCache<DailyForecastEntity>(key)
            if (cache != null) {
                return cache
            }
        }
        val result =
            weatherApiRequestManager.getDailyForecast(latitude, longitude, weatherProvider, requestId).fold(onSuccess = { response ->
                Result.success(weatherResponseMapperManager.mapDailyForecast(response, weatherProvider))
            }, onFailure = { error ->
                Result.failure(error)
            })

        if (!bypassCache) {
            cacheManager.put(key, result)
        }
        return result
    }

    override suspend fun getYesterdayWeather(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long, bypassCache: Boolean
    ): Result<YesterdayWeatherEntity> {
        val key = toKey(latitude, longitude, weatherProvider)
        if (!bypassCache) {
            val cache = getCache<YesterdayWeatherEntity>(key)
            if (cache != null) {
                return cache
            }
        }
        val result =
            weatherApiRequestManager.getYesterdayWeather(latitude, longitude, weatherProvider, requestId).fold(onSuccess = { response ->
                Result.success(weatherResponseMapperManager.mapYesterdayWeather(response, weatherProvider))
            }, onFailure = { error ->
                Result.failure(error)
            })

        if (!bypassCache) {
            cacheManager.put(key, result)
        }
        return result
    }

    private class CacheManager {
        private val cacheMap = ConcurrentHashMap<String, Cache>()
        private val cacheMaxTime: Duration = Duration.ofMinutes(2)
        private val cleaningInterval = Duration.ofMinutes(5).toKotlinDuration()
        private val lastSearchTimeMap = ConcurrentHashMap<String, LocalDateTime>()
        private val searchMaxInterval = Duration.ofSeconds(20)

        suspend fun startCacheCleaner() {
            while (true) {
                if (cacheMap.isNotEmpty()) {
                    val now = LocalDateTime.now()
                    for ((key, value) in cacheMap.entries) {
                        if (value.isExpired(now, cacheMaxTime)) {
                            value.clear()
                            cacheMap.remove(key)
                            lastSearchTimeMap.remove(key)
                        }
                    }
                }
                delay(cleaningInterval)
            }
        }

        fun get(key: String): CacheState {
            val cache = cacheMap[key] ?: return CacheState.Miss

            val now = LocalDateTime.now()
            val isValidSearch = lastSearchTimeMap[key]?.let { Duration.between(it, now) <= searchMaxInterval } ?: false
            if (!isValidSearch) {
                lastSearchTimeMap[key] = now
            }

            if (cache.isExpired(lastSearchTimeMap[key]!!, cacheMaxTime)) {
                cache.clear()
                cacheMap.remove(key)
                return CacheState.Expired
            }
            return CacheState.Valid(cache.cacheList)
        }

        fun put(key: String, value: Result<EntityModel>) {
            cacheMap.getOrPut(key) {
                Cache(LocalDateTime.now())
            }.add(value)
        }

        private data class Cache(
            val added: LocalDateTime
        ) {
            private val mutableList: MutableList<Result<EntityModel>> = mutableListOf()
            val cacheList: List<Result<EntityModel>>
                get() = mutableList

            fun add(value: Result<EntityModel>) {
                mutableList.add(value)
            }

            fun isExpired(now: LocalDateTime, cacheMaxTime: Duration): Boolean = Duration.between(added, now) > cacheMaxTime

            fun clear() {
                mutableList.clear()
            }
        }

        sealed interface CacheState {
            data object Expired : CacheState
            data object Miss : CacheState
            data class Valid(val list: List<Result<EntityModel>>) : CacheState
        }
    }


    private fun toKey(latitude: Double, longitude: Double, weatherProvider: WeatherProvider): String =
        "$latitude,$longitude,${weatherProvider.name}"

    override suspend fun initialize() {
        cacheManager.startCacheCleaner()
    }
}