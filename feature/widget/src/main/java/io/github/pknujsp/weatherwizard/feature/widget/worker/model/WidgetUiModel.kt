package io.github.pknujsp.weatherwizard.feature.widget.worker.model

import io.github.pknujsp.weatherwizard.core.domain.weather.ResponseState
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType

data class WidgetUiModel<T : ResponseState>(
    val widgetType: WidgetType, val appWidgetId: Int, val address: String, val updatedTime: String, val state: T
) : UiModel