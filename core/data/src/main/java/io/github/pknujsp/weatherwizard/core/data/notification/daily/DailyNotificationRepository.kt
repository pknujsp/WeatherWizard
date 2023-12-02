package io.github.pknujsp.weatherwizard.core.data.notification.daily

import io.github.pknujsp.weatherwizard.core.data.notification.daily.model.DailyNotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.data.notification.daily.model.DailyNotificationSettingsJsonEntity
import kotlinx.coroutines.flow.Flow

interface DailyNotificationRepository {
    suspend fun switch(id: Long, enabled: Boolean)
    fun getDailyNotifications(): Flow<List<NotificationSettingsEntity<DailyNotificationSettingsEntity>>>
    suspend fun getDailyNotification(id: Long): NotificationSettingsEntity<DailyNotificationSettingsEntity>

    suspend fun updateDailyNotification(entity: NotificationSettingsEntity<DailyNotificationSettingsEntity>)

    suspend fun deleteDailyNotification(id: Long)
}