package io.github.pknujsp.weatherwizard.feature.widget.worker.model

import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.Coordinate
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.feature.widget.summary.toSummaryUiModel
import java.time.ZonedDateTime

class EntityMapper(
    private val entities: List<ResponseEntity>, private val units: CurrentUnits, private val now: ZonedDateTime
) {
    private val dayNightCalculatorMap = entities.groupBy { it.coordinate }.mapValues {
        DayNightCalculator(it.key.latitude, it.key.longitude)
    }

    private val mappingCache = mutableMapOf<Triple<WidgetType, Coordinate, WeatherDataProvider>, WidgetUiState>()

    operator fun invoke(): Map<Triple<WidgetType, Coordinate, WeatherDataProvider>, WidgetUiState> {
        entities.forEach { entity ->
            val coordinate = entity.coordinate
            val dayNightCalculator = dayNightCalculatorMap[coordinate]!!

            val uiModel = when (entity.widgetType) {
                WidgetType.SUMMARY -> entity.toSummaryUiModel(units, dayNightCalculator, now)
                else -> throw IllegalArgumentException("Unknown widget type: ${entity.widgetType}")
            }

            mappingCache[Triple(entity.widgetType, coordinate, entity.weatherDataProvider)] = uiModel
        }

        return mappingCache
    }
}

sealed interface WidgetUiState {
    data object Failure : WidgetUiState
    data class Success(val uiModel: UiModel) : WidgetUiState
}