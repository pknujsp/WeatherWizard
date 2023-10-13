package io.github.pknujsp.weatherwizard.feature.notification.common

import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.UiState

interface RemoteViewsModel {
    suspend fun load(): UiState<UiModel>
}