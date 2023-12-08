package io.github.pknujsp.weatherwizard.core.data.weather

import io.github.pknujsp.weatherwizard.core.data.RepositoryInitializer
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapperManager
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestManager
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity
import java.util.UUID
import javax.inject.Inject
import kotlin.reflect.KClass

class WeatherDataRepositoryImpl @Inject constructor(
    private val weatherResponseMapperManager: WeatherResponseMapperManager,
    private val weatherApiRequestManager: WeatherApiRequestManager,
    private val cacheManager: CacheManager<EntityModel>
) : WeatherDataRepository, RepositoryInitializer {

    private suspend fun <T : EntityModel> getCache(
        key: String, cls: KClass<T>
    ): T? = when (val cacheState = cacheManager.get<T>(key)) {
        is CacheManager.CacheState.Hit -> {
            cacheState.value
        }

        else -> null
    }

    override suspend fun getWeatherData(
        requestWeatherData: RequestWeatherData, requestId: Long, bypassCache: Boolean
    ): Result<EntityModel> {
        val key = requestWeatherData.key()

        if (!bypassCache) {
            getCache(key, requestWeatherData.majorWeatherEntityType.entityClass)?.let {
                return Result.success(it)
            }
        }

        val result = when (requestWeatherData.majorWeatherEntityType) {
            MajorWeatherEntityType.CURRENT_CONDITION -> getCurrentWeather(requestWeatherData, requestId)
            MajorWeatherEntityType.HOURLY_FORECAST -> getHourlyForecast(requestWeatherData, requestId)
            MajorWeatherEntityType.DAILY_FORECAST -> getDailyForecast(requestWeatherData, requestId)
            else -> getYesterdayWeather(requestWeatherData, requestId)
        }

        if (!bypassCache and result.isSuccess) {
            cacheManager.put(key, result.getOrNull()!!)
        }
        return result
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


    override suspend fun initialize() {
        cacheManager.startCacheCleaner()
    }

    private fun RequestWeatherData.key(): String = "$majorWeatherEntityType-$latitude-$longitude-$weatherProvider"

    private val WeatherProvider.cacheMaxTimeInMinutes: Long
        get() = when (this) {
            is WeatherProvider.Kma -> 300_000 // 5 분
            is WeatherProvider.MetNorway -> 300_000 // 5 분
        }
}