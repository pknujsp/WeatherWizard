package io.github.pknujsp.weatherwizard.feature.widget.worker.model

import io.github.pknujsp.weatherwizard.core.model.UiModel

data class WidgetUiModel<T : WidgetUiState>(
    val appWidgetId: Int, val address: String, val updatedTime: String, val state: T
) : UiModel