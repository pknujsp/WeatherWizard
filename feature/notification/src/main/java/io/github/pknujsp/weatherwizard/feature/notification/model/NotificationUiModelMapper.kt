package io.github.pknujsp.weatherwizard.feature.notification.model

import io.github.pknujsp.weatherwizard.core.model.EntityModel

interface NotificationUiModelMapper<T : EntityModel> {
    fun mapToUiModel(model: T): RemoteViewUiState<T>
}