package io.github.pknujsp.weatherwizard.feature.widget.worker.model

import io.github.pknujsp.weatherwizard.core.model.UiModel

data class WidgetUiModel<T : UiModel>(
    val address: String, val updatedTime: String, val model: T
) : UiModel