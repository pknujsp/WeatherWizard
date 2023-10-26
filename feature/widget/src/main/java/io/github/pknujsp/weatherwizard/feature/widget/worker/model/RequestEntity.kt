package io.github.pknujsp.weatherwizard.feature.widget.worker.model

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.Coordinate
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataMajorCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import java.time.ZonedDateTime

class RequestEntity(
    val now: ZonedDateTime = ZonedDateTime.now(),
) {
    private val _requests: MutableMap<Coordinate, Request> = mutableMapOf()
    val requests: Map<Coordinate, Request> get() = _requests

    private var requestId: Long = now.toEpochSecond()

    fun addRequest(
        appWidgetId: Int,
        widgetType: WidgetType,
        coordinate: Coordinate,
        weatherProvider: WeatherDataProvider,
        address: String = "",
    ) {
        _requests.getOrPut(coordinate) {
            Request(address, coordinate)
        }.apply {
            addHeader(weatherProvider, appWidgetId, widgetType, requestId++)
        }
    }

    data class Request(
        val address: String, val coordinate: Coordinate
    ) {
        private val _headerMap: MutableMap<WeatherDataProvider, Header> = mutableMapOf()
        val headerMap: Map<WeatherDataProvider, Header> get() = _headerMap

        fun addHeader(
            weatherProvider: WeatherDataProvider, appWidgetId: Int, widgetType: WidgetType, requestId: Long
        ) {
            _headerMap.getOrPut(weatherProvider) {
                Header(requestId)
            }.addHeader(appWidgetId, widgetType)
        }

        fun toResponseEntity() = headerMap.flatMap { (weatherDataProvider, header) ->
            header.appWidgetIds.map { (widgetType, appWidgetId) ->
                ResponseEntity(address, coordinate, appWidgetId, widgetType, weatherDataProvider, header.getResponses(widgetType))
            }
        }

        data class Header(
            val requestId: Long,
        ) {
            var appWidgetIds: List<Pair<WidgetType, Int>> = emptyList()
                private set
            var categories: Set<WeatherDataMajorCategory> = emptySet()
                private set

            private var responses: List<Result<EntityModel>> = emptyList()
            private var zip: List<Pair<WeatherDataMajorCategory, EntityModel>> = emptyList()
            private var isCompleted: Boolean = false

            fun addHeader(
                appWidgetId: Int, widgetType: WidgetType
            ) {
                appWidgetIds = appWidgetIds + (widgetType to appWidgetId)
                this.categories = this.categories + widgetType.categories
            }

            fun addResponse(response: Result<EntityModel>) {
                responses = responses + response
            }

            fun getResponses(widgetType: WidgetType): List<EntityModel> {
                val list = if (isCompleted) {
                    zip
                } else {
                    isCompleted = true
                    if (responses.all { it.isSuccess }) {
                        zip = categories.map { category ->
                            when (category) {
                                WeatherDataMajorCategory.CURRENT_CONDITION -> category to responses.first {
                                    it.getOrThrow() is CurrentWeatherEntity
                                }.getOrThrow()

                                WeatherDataMajorCategory.HOURLY_FORECAST -> category to responses.first { it.getOrThrow() is HourlyForecastEntity }
                                    .getOrThrow()

                                WeatherDataMajorCategory.DAILY_FORECAST -> category to responses.first { it.getOrThrow() is DailyForecastEntity }
                                    .getOrThrow()

                                else -> throw IllegalArgumentException("Unknown category: $category")
                            }
                        }
                    }
                    zip
                }

                return list.filter { it.first in widgetType.categories }.map { it.second }
            }
        }
    }
}