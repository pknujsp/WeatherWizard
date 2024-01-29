package io.github.pknujsp.everyweather.core.data.notification.daily

import io.github.pknujsp.everyweather.core.data.notification.daily.model.DailyNotificationSettingsEntity
import io.github.pknujsp.everyweather.core.model.notification.NotificationSettingsEntity
import kotlinx.coroutines.flow.Flow

interface DailyNotificationRepository {
    suspend fun switch(id: Long, enabled: Boolean)
    fun getDailyNotifications(): Flow<List<NotificationSettingsEntity<DailyNotificationSettingsEntity>>>
    suspend fun getDailyNotification(id: Long): NotificationSettingsEntity<DailyNotificationSettingsEntity>

    suspend fun updateDailyNotification(entity: NotificationSettingsEntity<DailyNotificationSettingsEntity>)

    suspend fun createDailyNotification(entity: NotificationSettingsEntity<DailyNotificationSettingsEntity>): Long

    suspend fun deleteDailyNotification(id: Long)
}