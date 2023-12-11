package io.github.pknujsp.weatherwizard.core.data.weather

import io.github.pknujsp.weatherwizard.core.data.RepositoryCacheManager
import io.github.pknujsp.weatherwizard.core.data.cache.CacheCleaner
import io.github.pknujsp.weatherwizard.core.data.cache.CacheManager
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapperManager
import io.github.pknujsp.weatherwizard.core.data.weather.model.CachedWeatherModel
import io.github.pknujsp.weatherwizard.core.data.weather.model.WeatherModel
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestManager
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.ApiResponseModel
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

internal class WeatherDataRepositoryImpl(
    private val weatherResponseMapperManager: WeatherResponseMapperManager<EntityModel>,
    private val weatherApiRequestManager: WeatherApiRequestManager<ApiResponseModel>,
    cacheManager: CacheManager<Int, CachedWeatherModel>,
    cacheCleaner: CacheCleaner
) : WeatherDataRepository, RepositoryCacheManager<Int, CachedWeatherModel>(cacheCleaner, cacheManager) {

    private suspend fun getCache(
        key: Int, requestWeatherData: RequestWeatherData
    ): WeatherModel? = when (val cacheState = cacheManager.get(key)) {
        is CacheManager.CacheState.Hit -> {
            cacheState.value.export(requestWeatherData.majorWeatherEntityTypes)
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

        val loads = supervisorScope {
            requestWeatherData.weatherProvider.majorWeatherEntityTypes.map { majorWeatherEntityType ->
                async {
                    majorWeatherEntityType to weatherApiRequestManager.get(requestWeatherData.latitude,
                        requestWeatherData.longitude,
                        requestWeatherData.weatherProvider,
                        majorWeatherEntityType,
                        requestId).map {
                        weatherResponseMapperManager.map(it, requestWeatherData.weatherProvider, majorWeatherEntityType)
                    }
                }
            }.map {
                it.await()
            }
        }

        val allSuccess = loads.all { it.second.isSuccess }
        return if (allSuccess) {
            cacheManager.put(key, CachedWeatherModel().apply {
                loads.forEach { (type, value) ->
                    put(type, value.getOrThrow())
                }
            })
            Result.success(WeatherModel(loads.filter { it.first in requestWeatherData.majorWeatherEntityTypes }
                .map { it.second.getOrThrow() }))
        } else {
            Result.failure(Throwable())
        }
    }


    private fun RequestWeatherData.key() = hashCode()

}