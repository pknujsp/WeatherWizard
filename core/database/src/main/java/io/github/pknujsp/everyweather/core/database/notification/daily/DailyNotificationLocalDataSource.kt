package io.github.pknujsp.everyweather.core.database.notification.daily

import io.github.pknujsp.everyweather.core.database.notification.NotificationDto
import kotlinx.coroutines.flow.Flow

interface DailyNotificationLocalDataSource {
    fun getDailyNotifications(): Flow<List<NotificationDto>>

    suspend fun getDailyNotification(id: Long): NotificationDto

    suspend fun updateDailyNotification(notificationDto: NotificationDto): Long

    suspend fun removeDailyNotification(id: Long)

    suspend fun switch(
        id: Long,
        enabled: Boolean,
    )
}
