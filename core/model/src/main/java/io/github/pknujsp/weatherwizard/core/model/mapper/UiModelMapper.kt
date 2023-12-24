package io.github.pknujsp.weatherwizard.core.model.mapper

import io.github.pknujsp.weatherwizard.core.model.Model
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.settings.CurrentUnits

interface UiModelMapper<T : Model, O : UiModel> {
    fun mapToUiModel(model: T, units: CurrentUnits): O
}