package io.github.pknujsp.weatherwizard.core.data.notification.daily

import io.github.pknujsp.weatherwizard.core.model.notification.NotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.data.notification.daily.model.DailyNotificationSettingsJsonEntity
import kotlinx.coroutines.flow.Flow

interface DailyNotificationRepository {
    suspend fun switch(id: Long, enabled: Boolean)
    fun getDailyNotifications(): Flow<List<NotificationSettingsEntity<DailyNotificationSettingsJsonEntity>>>
    suspend fun getDailyNotification(id: Long): NotificationSettingsEntity<DailyNotificationSettingsJsonEntity>

    suspend fun updateDailyNotification(entity: NotificationSettingsEntity<DailyNotificationSettingsJsonEntity>)

    suspend fun deleteDailyNotification(id: Long)
}