package io.github.pknujsp.weatherwizard.core.data.weather

import io.github.pknujsp.weatherwizard.core.data.RepositoryCacheManager
import io.github.pknujsp.weatherwizard.core.data.cache.CacheCleaner
import io.github.pknujsp.weatherwizard.core.data.cache.CacheManager
import io.github.pknujsp.weatherwizard.core.data.weather.mapper.WeatherResponseMapperManager
import io.github.pknujsp.weatherwizard.core.data.weather.model.CachedWeatherModel
import io.github.pknujsp.weatherwizard.core.data.weather.model.WeatherModel
import io.github.pknujsp.weatherwizard.core.data.weather.request.WeatherApiRequestManager
import io.github.pknujsp.weatherwizard.core.model.ApiResponseModel
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

internal class WeatherDataRepositoryImpl(
    private val weatherResponseMapperManager: WeatherResponseMapperManager<WeatherEntityModel>,
    private val weatherApiRequestManager: WeatherApiRequestManager<ApiResponseModel>,
    cacheManager: CacheManager<Int, CachedWeatherModel>,
    cacheCleaner: CacheCleaner,
    private val jsonParser: JsonParser
) : WeatherDataRepository, RepositoryCacheManager<Int, CachedWeatherModel>(cacheCleaner, cacheManager) {

    private suspend fun getCache(
        key: Int, requestWeatherData: RequestWeatherData
    ): List<Pair<MajorWeatherEntityType, ApiResponseModel>>? = when (val cacheState = cacheManager.get(key)) {
        is CacheManager.CacheState.Hit -> {
            cacheState.value.export(requestWeatherData.majorWeatherEntityTypes)
        }

        else -> null
    }

    private suspend fun load(
        requestWeatherData: RequestWeatherData, requestId: Long, bypassCache: Boolean
    ): Result<List<Pair<MajorWeatherEntityType, ApiResponseModel>>> {
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
                        requestId)
                }
            }.map {
                it.await()
            }
        }

        val allSuccess = loads.all { it.second.isSuccess }
        return if (allSuccess) {
            val entities = loads.map { pair ->
                pair.first to pair.second.getOrThrow()
            }
            cacheManager.put(key, CachedWeatherModel().apply {
                entities.forEach { (type, value) ->
                    put(type, value)
                }
            })

            Result.success(entities)
        } else {
            Result.failure(Throwable())
        }
    }


    override suspend fun getWeatherData(
        requestWeatherData: RequestWeatherData, requestId: Long, bypassCache: Boolean
    ): Result<WeatherModel> {
        val result = load(requestWeatherData, requestId, bypassCache)
        return result.map { list ->
            WeatherModel(list.filter { it.first in requestWeatherData.majorWeatherEntityTypes }.map {
                weatherResponseMapperManager.map(it.second, requestWeatherData.weatherProvider, it.first)
            })
        }
    }

    override suspend fun getWeatherDataByBytes(
        requestWeatherData: RequestWeatherData, requestId: Long, bypassCache: Boolean
    ): Result<WeatherModel> {
        return load(requestWeatherData, requestId, bypassCache).map { list ->
            WeatherModel(list.filter { it.first in requestWeatherData.majorWeatherEntityTypes }.map {
                weatherResponseMapperManager.map(it.second, it.first)
            })
        }
    }

    private fun RequestWeatherData.key() = hashCode()

}