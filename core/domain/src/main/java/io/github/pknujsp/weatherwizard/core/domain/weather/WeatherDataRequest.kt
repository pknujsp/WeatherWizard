package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import java.time.ZonedDateTime
import java.util.concurrent.atomic.AtomicLong

class WeatherDataRequest(
    val requestedTime: ZonedDateTime = ZonedDateTime.now(),
) {
    private val sameLocationMap: MutableMap<Coordinate, SameLocation> = mutableMapOf()

    val finalRequests
        get() = sameLocationMap.values.flatMap { request ->
            request.requests.map {
                Request(
                    requestId = request.getRequestId(it.first),
                    location = request.coordinate,
                    weatherProvider = it.first,
                    weatherDataMajorCategories = it.second,
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
        coordinate: Coordinate,
        categories: Set<MajorWeatherEntityType>,
        weatherProvider: WeatherProvider,
    ): Long {
        return sameLocationMap.getOrPut(coordinate) {
            SameLocation(coordinate)
        }.run {
            addWeatherProviderWithCategories(weatherProvider, categories)
        }
    }

    private data class SameLocation(
        val coordinate: Coordinate
    ) {
        private val requestMap: MutableMap<WeatherProvider, MutableSet<MajorWeatherEntityType>> = mutableMapOf()
        private val requestIdMap: MutableMap<WeatherProvider, Long> = mutableMapOf()

        val requests
            get() = requestMap.map {
                it.key to it.value.toSet()
            }

        fun getRequestId(weatherProvider: WeatherProvider) = requestIdMap.getValue(weatherProvider)

        fun addWeatherProviderWithCategories(
            weatherProvider: WeatherProvider, categories: Set<MajorWeatherEntityType>,
        ): Long {
            if (weatherProvider !in requestMap) {
                requestIdMap[weatherProvider] = baseRequestId.getAndIncrement()
            }
            requestMap.getOrPut(weatherProvider) {
                mutableSetOf()
            }.addAll(categories)

            return requestIdMap.getValue(weatherProvider)
        }
    }

    data class Request(
        val requestId: Long,
        val location: Coordinate,
        val weatherProvider: WeatherProvider,
        val weatherDataMajorCategories: Set<MajorWeatherEntityType>,
    )

    data class Coordinate(
        val latitude: Double = 0.0, val longitude: Double = 0.0
    )
}