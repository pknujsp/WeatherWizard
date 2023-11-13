package io.github.pknujsp.weatherwizard.core.domain.weather

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.Coordinate
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataMajorCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType

data class ResponseEntity(
    val requestId: Long,
    val coordinate: Coordinate,
    val weatherDataProvider: WeatherDataProvider,
    val weatherDataMajorCategories: Set<WeatherDataMajorCategory>,
    val responses: List<EntityModel>
) : EntityModel {
    inline fun <reified T : EntityModel> toEntity(): T? = responses.firstOrNull { it is T } as? T
}

sealed interface ResponseState {
    data object Failure : ResponseState
    data class Success(val entity: ResponseEntity) : ResponseState
    data class PartiallySuccess(val entity: ResponseEntity, val failedCategories: List<WeatherDataMajorCategory>) : ResponseState
}