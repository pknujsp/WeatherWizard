package io.github.pknujsp.weatherwizard.feature.notification.ongoing.model

import io.github.pknujsp.weatherwizard.core.model.RemoteViewUiModel
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationIconType
import io.github.pknujsp.weatherwizard.feature.notification.model.RemoteViewUiState

data class OngoingNotificationRemoteViewUiState<T : RemoteViewUiModel>(
    val notificationIconType: NotificationIconType,
    override val uiModel: T,
) : RemoteViewUiState<T>