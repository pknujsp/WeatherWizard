package io.github.pknujsp.weatherwizard.core.data.weather

import io.github.pknujsp.weatherwizard.core.data.RepositoryCacheManager
import io.github.pknujsp.weatherwizard.core.data.cache.CacheCleaner
import io.github.pknujsp.weatherwizard.core.data.cache.CacheManager
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapperManager
import io.github.pknujsp.weatherwizard.core.data.weather.model.CachedWeatherModel
import io.github.pknujsp.weatherwizard.core.data.weather.model.WeatherModel
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestManager
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity
import javax.inject.Inject

class WeatherDataRepositoryImpl @Inject constructor(
    private val weatherResponseMapperManager: WeatherResponseMapperManager,
    private val weatherApiRequestManager: WeatherApiRequestManager,
    cacheManager: CacheManager<Int, CachedWeatherModel>,
    cacheCleaner: CacheCleaner
) : WeatherDataRepository, RepositoryCacheManager<Int, CachedWeatherModel>(cacheCleaner, cacheManager) {


    private suspend fun getCache(
        key: Int, requestWeatherData: RequestWeatherData
    ): WeatherModel? = when (val cacheState = cacheManager.get(key)) {
        is CacheManager.CacheState.Hit -> {
            if (requestWeatherData.majorWeatherEntityTypes in cacheState.value) {
                cacheState.value.export(requestWeatherData.majorWeatherEntityTypes)
            } else {
                null
            }
        }

        else -> null
    }

    override suspend fun getWeatherData(
        requestWeatherData: RequestWeatherData, requestId: Long, bypassCache: Boolean
    ): Result<WeatherModel> {
        val key = requestWeatherData.key()

        if (!bypassCache) {
            getCache(key, requestWeatherData)?.let {
                return Result.success(it)
            }
        }

        val result = load(requestWeatherData, requestId)
        val allSuccess = result.all { it.second.isSuccess }

        if (!bypassCache and allSuccess) {
            cacheManager.put(key, CachedWeatherModel(requestWeatherData).apply {
                result.forEach { (type, value) ->
                    put(type, value.getOrThrow())
                }
            })
        }

        return if (allSuccess) {
            Result.success(WeatherModel(result.map { it.second.getOrThrow() }))
        } else {
            Result.failure(Throwable())
        }
    }

    private suspend fun load(requestWeatherData: RequestWeatherData, requestId: Long) =
        requestWeatherData.majorWeatherEntityTypes.map { type ->
            type to when (type) {
                MajorWeatherEntityType.CURRENT_CONDITION -> getCurrentWeather(requestWeatherData, requestId)
                MajorWeatherEntityType.HOURLY_FORECAST -> getHourlyForecast(requestWeatherData, requestId)
                MajorWeatherEntityType.DAILY_FORECAST -> getDailyForecast(requestWeatherData, requestId)
                else -> getYesterdayWeather(requestWeatherData, requestId)
            }
        }

    private suspend fun getCurrentWeather(
        requestWeatherData: RequestWeatherData, requestId: Long
    ): Result<CurrentWeatherEntity> = weatherApiRequestManager.getCurrentWeather(requestWeatherData.latitude,
        requestWeatherData.longitude,
        requestWeatherData.weatherProvider,
        requestId).map {
        weatherResponseMapperManager.mapCurrentWeather(it, requestWeatherData.weatherProvider)
    }


    private suspend fun getHourlyForecast(
        requestWeatherData: RequestWeatherData, requestId: Long
    ): Result<HourlyForecastEntity> = weatherApiRequestManager.getHourlyForecast(requestWeatherData.latitude,
        requestWeatherData.longitude,
        requestWeatherData.weatherProvider,
        requestId).map {
        weatherResponseMapperManager.mapHourlyForecast(it, requestWeatherData.weatherProvider)
    }


    private suspend fun getDailyForecast(
        requestWeatherData: RequestWeatherData, requestId: Long
    ): Result<DailyForecastEntity> = weatherApiRequestManager.getDailyForecast(requestWeatherData.latitude,
        requestWeatherData.longitude,
        requestWeatherData.weatherProvider,
        requestId).map {
        weatherResponseMapperManager.mapDailyForecast(it, requestWeatherData.weatherProvider)
    }

    private suspend fun getYesterdayWeather(
        requestWeatherData: RequestWeatherData, requestId: Long
    ): Result<YesterdayWeatherEntity> = weatherApiRequestManager.getYesterdayWeather(requestWeatherData.latitude,
        requestWeatherData.longitude,
        requestWeatherData.weatherProvider,
        requestId).map {
        weatherResponseMapperManager.mapYesterdayWeather(it, requestWeatherData.weatherProvider)
    }


    private fun RequestWeatherData.key() = hashCode()

}