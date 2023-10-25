package io.github.pknujsp.weatherwizard.feature.widget.worker.model

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataMajorCategory
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType

class ResponseEntity {
    private val _responses: MutableMap<Long, Response> = mutableMapOf()
    val responses: Map<Long, Response> get() = _responses

    fun addResponse(
        requestId: Long,
        weatherDataMajorCategory: WeatherDataMajorCategory,
        response: Result<EntityModel>,
        appWidgetIds: List<Pair<WidgetType, Int>>
    ) {
        _responses.getOrPut(requestId) {
            Response(appWidgetIds)
        }.result.add(weatherDataMajorCategory to response)
    }

    data class Response(
        val appWidgetIds: List<Pair<WidgetType, Int>>,
        val result: MutableList<Pair<WeatherDataMajorCategory, Result<EntityModel>>> = mutableListOf()
    )
}