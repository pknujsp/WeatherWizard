package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import java.util.concurrent.atomic.AtomicLong

object WeatherDataRequest {

    class Builder {
        companion object {
            private val baseRequestId: AtomicLong = AtomicLong(0)
        }

        private val sameLocationMap: MutableMap<Coordinate, SameLocation> = mutableMapOf()

        fun build(): List<Request> = sameLocationMap.entries.flatMap { (coordinate, request) ->
            val requestId = baseRequestId.incrementAndGet()
            request.requests.map {
                Request(
                    requestId = requestId,
                    coordinate = coordinate,
                    weatherProvider = it.first,
                    categories = it.second,
                )
            }
        }

        fun add(
            coordinate: Coordinate,
            categories: Array<MajorWeatherEntityType>,
            weatherProvider: WeatherProvider,
        ) {
            sameLocationMap.getOrDefault(coordinate, SameLocation()).add(weatherProvider, categories)
        }
    }

    private class SameLocation {
        private val requestMap: MutableMap<WeatherProvider, MutableSet<MajorWeatherEntityType>> = mutableMapOf()

        val requests
            get() = requestMap.map {
                it.key to it.value.toSet()
            }

        fun add(
            weatherProvider: WeatherProvider, categories: Array<MajorWeatherEntityType>,
        ) {
            requestMap.getOrDefault(weatherProvider, mutableSetOf()).addAll(categories)
        }
    }

    data class Request(
        val requestId: Long,
        val coordinate: Coordinate,
        val weatherProvider: WeatherProvider,
        val categories: Set<MajorWeatherEntityType>,
    )

    data class Coordinate(
        val latitude: Double, val longitude: Double, val address: String
    )
}

/*
* val state = WidgetRemoteViewUiState(
                    widget = widget,
                    lastUpdated = if (response is WeatherResponseState.Success) response.entity.responseTime else null,
                    address = locationMap[response.location],
                    isSuccessful = response is WeatherResponseState.Success,
                    model = if (response is WeatherResponseState.Success) response.entity else null,
                    latitude = response.location.latitude,
                    longitude = response.location.longitude,
                )
* */