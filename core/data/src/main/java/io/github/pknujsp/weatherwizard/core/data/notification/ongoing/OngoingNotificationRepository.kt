package io.github.pknujsp.weatherwizard.core.data.notification.ongoing

import io.github.pknujsp.weatherwizard.core.model.notification.NotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.model.OngoingNotificationSettingsEntity

interface OngoingNotificationRepository {
    suspend fun getOngoingNotification(): NotificationSettingsEntity<OngoingNotificationSettingsEntity>
    suspend fun updateOngoingNotification(notificationSettingsEntity: NotificationSettingsEntity<OngoingNotificationSettingsEntity>): Boolean
    suspend fun switch(enabled: Boolean)
}