package io.github.pknujsp.weatherwizard.feature.notification.common

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.UiState

abstract class RemoteViewModel {
    abstract suspend fun load(): UiState<UiModel>
}