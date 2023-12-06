package io.github.pknujsp.weatherwizard.core.data.weather

import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapperManager
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestManager
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity
import javax.inject.Inject
import kotlin.reflect.KClass

class WeatherDataRepositoryImpl @Inject constructor(
    private val weatherResponseMapperManager: WeatherResponseMapperManager,
    private val weatherApiRequestManager: WeatherApiRequestManager,
    private val cacheManager: CacheManager<EntityModel>
) : WeatherDataRepository, WeatherDataRepositoryInitializer {

    private suspend fun <T : EntityModel> getCache(
        key: String, cls: KClass<T>
    ): T? = when (val cacheState = cacheManager.get(key, cls)) {
        is CacheManager.CacheState.Valid -> {
            cacheState.value
        }

        else -> null
    }

    override suspend fun getWeatherData(
        majorWeatherEntityType: MajorWeatherEntityType,
        latitude: Double,
        longitude: Double,
        weatherProvider: WeatherProvider,
        requestId: Long,
        bypassCache: Boolean
    ): Result<EntityModel> {
        val key = toKey(latitude, longitude, weatherProvider)

        if (!bypassCache) {
            getCache(key, majorWeatherEntityType.entityClass)?.let {
                return Result.success(it)
            }
        }

        val result = when (majorWeatherEntityType) {
            MajorWeatherEntityType.CURRENT_CONDITION -> getCurrentWeather(latitude, longitude, weatherProvider, requestId)
            MajorWeatherEntityType.HOURLY_FORECAST -> getHourlyForecast(latitude, longitude, weatherProvider, requestId)
            MajorWeatherEntityType.DAILY_FORECAST -> getDailyForecast(latitude, longitude, weatherProvider, requestId)
            MajorWeatherEntityType.YESTERDAY_WEATHER -> getYesterdayWeather(latitude, longitude, weatherProvider, requestId)
            else -> throw IllegalArgumentException("Invalid majorWeatherEntityType: $majorWeatherEntityType")
        }

        if (!bypassCache and result.isSuccess) {
            cacheManager.put(key, result.getOrThrow())
        }
        return result
    }

    private suspend fun getCurrentWeather(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ): Result<CurrentWeatherEntity> =
        weatherApiRequestManager.getCurrentWeather(latitude, longitude, weatherProvider, requestId).fold(onSuccess = { response ->
            Result.success(weatherResponseMapperManager.mapCurrentWeather(response, weatherProvider))
        }, onFailure = { error ->
            Result.failure(error)
        })


    private suspend fun getHourlyForecast(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ): Result<HourlyForecastEntity> =
        weatherApiRequestManager.getHourlyForecast(latitude, longitude, weatherProvider, requestId).fold(onSuccess = { response ->
            Result.success(weatherResponseMapperManager.mapHourlyForecast(response, weatherProvider))
        }, onFailure = { error ->
            Result.failure(error)
        })


    private suspend fun getDailyForecast(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ): Result<DailyForecastEntity> =
        weatherApiRequestManager.getDailyForecast(latitude, longitude, weatherProvider, requestId).fold(onSuccess = { response ->
            Result.success(weatherResponseMapperManager.mapDailyForecast(response, weatherProvider))
        }, onFailure = { error ->
            Result.failure(error)
        })


    private suspend fun getYesterdayWeather(
        latitude: Double, longitude: Double, weatherProvider: WeatherProvider, requestId: Long
    ): Result<YesterdayWeatherEntity> =
        weatherApiRequestManager.getYesterdayWeather(latitude, longitude, weatherProvider, requestId).fold(onSuccess = { response ->
            Result.success(weatherResponseMapperManager.mapYesterdayWeather(response, weatherProvider))
        }, onFailure = { error ->
            Result.failure(error)
        })

    private fun toKey(latitude: Double, longitude: Double, weatherProvider: WeatherProvider): String =
        "$latitude,$longitude,${weatherProvider.name}"

    override suspend fun initialize() {
        cacheManager.startCacheCleaner()
    }
}