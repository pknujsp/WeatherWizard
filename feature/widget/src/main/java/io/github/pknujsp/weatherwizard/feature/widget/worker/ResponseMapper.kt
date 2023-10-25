package io.github.pknujsp.weatherwizard.feature.widget.worker

import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.CurrentUnits
import io.github.pknujsp.weatherwizard.feature.widget.worker.model.RequestEntity
import io.github.pknujsp.weatherwizard.feature.widget.worker.model.ResponseEntity
import io.github.pknujsp.weatherwizard.feature.widget.worker.model.WidgetUiModel
import java.time.ZonedDateTime

class ResponseMapper(
    val requestEntity: RequestEntity,
    val responseEntity: ResponseEntity,
    val now: ZonedDateTime,
    val dayNightCalculator: DayNightCalculator,
    val units: CurrentUnits
) {

    fun map(): Map<Long, WidgetUiModel<UiModel>> {
        val widgetUiModels = mutableMapOf<Long, WidgetUiModel<UiModel>>()

        responseEntity.responses.forEach { (requestId, list) ->

        }

        return widgetUiModels
    }

    private fun mapSummary()
}