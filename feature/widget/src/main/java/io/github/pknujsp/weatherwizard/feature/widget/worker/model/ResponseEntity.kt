package io.github.pknujsp.weatherwizard.feature.widget.worker.model

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataMajorCategory

class ResponseEntity {
    private val _responses: MutableMap<Long, MutableList<Pair<WeatherDataMajorCategory, Result<EntityModel>>>> = mutableMapOf()
    val responses: Map<Long, List<Pair<WeatherDataMajorCategory, Result<EntityModel>>>> get() = _responses

    fun addResponse(requestId: Long, weatherDataMajorCategory: WeatherDataMajorCategory, response: Result<EntityModel>) {
        _responses.getOrPut(requestId) {
            mutableListOf()
        }.add(weatherDataMajorCategory to response)
    }
}