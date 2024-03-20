package io.github.pknujsp.everyweather.core.data.notification.ongoing

import io.github.pknujsp.everyweather.core.data.notification.ongoing.model.OngoingNotificationSettingsEntity
import io.github.pknujsp.everyweather.core.model.notification.NotificationSettingsEntity

interface OngoingNotificationRepository {
    suspend fun getOngoingNotification(): NotificationSettingsEntity<OngoingNotificationSettingsEntity>

    suspend fun updateOngoingNotification(
        notificationSettingsEntity: NotificationSettingsEntity<OngoingNotificationSettingsEntity>,
    ): Boolean

    suspend fun switch(enabled: Boolean)
}
