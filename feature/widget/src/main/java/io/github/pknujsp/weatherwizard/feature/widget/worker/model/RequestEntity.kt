package io.github.pknujsp.weatherwizard.feature.widget.worker.model

import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataMajorCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import java.time.ZonedDateTime

class RequestEntity(
    val now: ZonedDateTime = ZonedDateTime.now(),
) {
    private val _requests: MutableMap<Pair<Float, Float>, Parameter> = mutableMapOf()
    val requests: Map<Pair<Float, Float>, Parameter> get() = _requests

    private var requestId: Long = now.toEpochSecond()

    fun addRequest(
        appWidgetId: Int,
        latitude: Float,
        longitude: Float,
        address: String = "",
        weatherProvider: WeatherDataProvider,
        categories: Array<WeatherDataMajorCategory>,
    ) {
        _requests.getOrPut(latitude to longitude) {
            Parameter(address, latitude, longitude)
        }.apply {
            addProvider(weatherProvider, appWidgetId, categories, requestId++)
        }
    }

    data class Parameter(
        var address: String,
        val latitude: Float,
        val longitude: Float,
    ) {
        private val _providerMap: MutableMap<WeatherDataProvider, Provider> = mutableMapOf()
        val providerMap: Map<WeatherDataProvider, Provider> get() = _providerMap

        fun addProvider(
            weatherProvider: WeatherDataProvider,
            appWidgetId: Int,
            categories: Array<WeatherDataMajorCategory>,
            requestId: Long
        ) {
            _providerMap.getOrPut(weatherProvider) {
                Provider(
                    requestId = requestId
                )
            }.apply {
                appWidgetIds = appWidgetIds + appWidgetId
                this.categories = this.categories + categories
            }
        }

        data class Provider(
            var appWidgetIds: List<Int> = emptyList(),
            var categories: Set<WeatherDataMajorCategory> = emptySet(),
            val requestId: Long
        )
    }
}