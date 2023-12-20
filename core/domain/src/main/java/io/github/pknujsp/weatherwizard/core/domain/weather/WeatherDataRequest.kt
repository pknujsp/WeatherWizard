package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import java.time.ZonedDateTime
import java.util.concurrent.atomic.AtomicLong

class WeatherDataRequest(
    val requestedTime: ZonedDateTime = ZonedDateTime.now(),
    private val modelType: ModelType = ModelType.NORMAL,
) {
    private val sameLocationMap: MutableMap<LocationTypeModel, SameLocation> = mutableMapOf()
    val finalRequests
        get() = sameLocationMap.values.flatMap { request ->
            request.requests.map {
                Request(
                    requestId = request.getRequestId(it.first),
                    location = request.location,
                    weatherProvider = it.first,
                    weatherDataMajorCategories = it.second,
                    modelType = modelType,
                )
            }
        }

    private companion object {
        val baseRequestId: AtomicLong = AtomicLong(0)
    }

    /**
     * @return requestId
     */
    fun addRequest(
        location: LocationTypeModel,
        weatherDataCategories: Set<MajorWeatherEntityType>,
        weatherProvider: WeatherProvider,
    ): Long {
        return sameLocationMap.getOrPut(location) {
            SameLocation(location)
        }.run {
            addWeatherProviderWithCategories(weatherProvider, weatherDataCategories)
        }
    }

    private data class SameLocation(
        val location: LocationTypeModel
    ) {
        private val requestMap: MutableMap<WeatherProvider, MutableSet<MajorWeatherEntityType>> = mutableMapOf()
        private val requestIdMap: MutableMap<WeatherProvider, Long> = mutableMapOf()

        val requests
            get() = requestMap.map {
                it.key to it.value.toSet()
            }

        fun getRequestId(weatherProvider: WeatherProvider) = requestIdMap.getValue(weatherProvider)

        fun addWeatherProviderWithCategories(
            weatherProvider: WeatherProvider, weatherDataCategories: Set<MajorWeatherEntityType>,
        ): Long {
            if (weatherProvider !in requestMap) {
                requestIdMap[weatherProvider] = baseRequestId.getAndIncrement()
            }
            requestMap.getOrPut(weatherProvider) {
                mutableSetOf()
            }.addAll(weatherDataCategories)

            return requestIdMap.getValue(weatherProvider)
        }
    }

    data class Request(
        val requestId: Long,
        val location: LocationTypeModel,
        val weatherProvider: WeatherProvider,
        val weatherDataMajorCategories: Set<MajorWeatherEntityType>,
        val modelType: ModelType,
    )

    enum class ModelType {
        NORMAL, BYTES,
    }
}