package io.github.pknujsp.everyweather.core.model.mapper

import io.github.pknujsp.everyweather.core.model.Model
import io.github.pknujsp.everyweather.core.model.UiModel
import io.github.pknujsp.everyweather.core.model.settings.CurrentUnits

interface UiModelMapper<T : Model, O : UiModel> {
    fun mapToUiModel(
        model: T,
        units: CurrentUnits,
    ): O
}
