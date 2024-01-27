package io.github.pknujsp.everyweather.core.data.weather

import io.github.pknujsp.everyweather.core.data.RepositoryCacheManager
import io.github.pknujsp.everyweather.core.data.cache.CacheCleaner
import io.github.pknujsp.everyweather.core.data.cache.CacheManager
import io.github.pknujsp.everyweather.core.data.weather.mapper.WeatherResponseMapperManager
import io.github.pknujsp.everyweather.core.data.weather.model.CachedWeatherModel
import io.github.pknujsp.everyweather.core.data.weather.model.WeatherModel
import io.github.pknujsp.everyweather.core.data.weather.request.WeatherApiRequestManager
import io.github.pknujsp.everyweather.core.model.ApiResponseModel
import io.github.pknujsp.everyweather.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.everyweather.core.model.weather.common.MajorWeatherEntityType
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

internal class WeatherDataRepositoryImpl(
    private val weatherResponseMapperManager: WeatherResponseMapperManager<WeatherEntityModel>,
    private val weatherApiRequestManager: WeatherApiRequestManager<ApiResponseModel>,
    cacheManager: CacheManager<Int, CachedWeatherModel>,
    cacheCleaner: CacheCleaner,
) : WeatherDataRepository, RepositoryCacheManager<Int, CachedWeatherModel>(cacheCleaner, cacheManager) {

    private suspend fun getCache(
        key: Int, requestWeatherData: RequestWeatherData
    ): List<Pair<MajorWeatherEntityType, WeatherEntityModel>>? = when (val cacheState = cacheManager.get(key)) {
        is CacheManager.CacheState.Hit -> cacheState.value.export(requestWeatherData.majorWeatherEntityTypes)
        else -> null
    }

    private suspend fun load(
        requestWeatherData: RequestWeatherData, requestId: Long, bypassCache: Boolean
    ): Result<List<Pair<MajorWeatherEntityType, WeatherEntityModel>>> {
        val key = requestWeatherData.key()

        if (!bypassCache) {
            getCache(key, requestWeatherData)?.run {
                return Result.success(this)
            }
        }

        val responses = coroutineScope {
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

        return if (responses.all { it.second.isSuccess }) {
            val entities = responses.map { (type, model) ->
                type to weatherResponseMapperManager.map(model.getOrThrow(), requestWeatherData.weatherProvider, type)
            }

            cacheManager.put(key, CachedWeatherModel().apply {
                for (pair in entities) {
                    put(pair.first, pair.second)
                }
            })

            Result.success(entities)
        } else {
            Result.failure(Throwable())
        }
    }


    override suspend fun getWeatherData(
        requestWeatherData: RequestWeatherData, requestId: Long, bypassCache: Boolean
    ): Result<WeatherModel> = load(requestWeatherData, requestId, bypassCache).map { list ->
        WeatherModel(list.filter { it.first in requestWeatherData.majorWeatherEntityTypes }.map { it.second })
    }


    private fun RequestWeatherData.key() = hashCode()
}