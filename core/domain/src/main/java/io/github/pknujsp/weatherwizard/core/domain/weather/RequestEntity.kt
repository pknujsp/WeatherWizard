package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.model.coordinate.Coordinate
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataMajorCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import java.time.ZonedDateTime
import java.util.concurrent.atomic.AtomicLong

class RequestEntity(
    val requestedTime: ZonedDateTime = ZonedDateTime.now(),
) {
    private val sameCoordinateMap: MutableMap<Coordinate, SameCoordinate> = mutableMapOf()
    val requests
        get() = sameCoordinateMap.values.flatMap { request ->
            request.requests.map {
                Request(
                    requestId = baseRequestId.getAndIncrement(),
                    coordinate = request.coordinate,
                    weatherDataProvider = it.first,
                    weatherDataMajorCategories = it.second,
                )
            }
        }

    private companion object {
        val baseRequestId: AtomicLong = AtomicLong(0)
    }

    fun addRequest(
        coordinate: Coordinate,
        weatherDataCategories: Set<WeatherDataMajorCategory>,
        weatherProvider: WeatherDataProvider,
    ) {
        sameCoordinateMap.getOrPut(coordinate) {
            SameCoordinate(coordinate)
        }.apply {
            addWeatherProviderWithCategories(weatherProvider, weatherDataCategories)
        }
    }

    private data class SameCoordinate(
        val coordinate: Coordinate
    ) {
        private val requestMap: MutableMap<WeatherDataProvider, MutableSet<WeatherDataMajorCategory>> = mutableMapOf()
        val requests = requestMap.map {
            it.key to it.value.toSet()
        }

        fun addWeatherProviderWithCategories(
            weatherProvider: WeatherDataProvider, weatherDataCategories: Set<WeatherDataMajorCategory>,
        ) {
            requestMap.getOrPut(weatherProvider) {
                mutableSetOf()
            }.addAll(weatherDataCategories)
        }
    }

    data class Request(
        val requestId: Long,
        val coordinate: Coordinate,
        val weatherDataProvider: WeatherDataProvider,
        val weatherDataMajorCategories: Set<WeatherDataMajorCategory>,
    )
}