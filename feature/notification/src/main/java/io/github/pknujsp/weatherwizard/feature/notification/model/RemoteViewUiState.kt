package io.github.pknujsp.weatherwizard.feature.notification.model

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel

interface RemoteViewUiState<T: RemoteViewUiModel>{
    val uiModel: T
}