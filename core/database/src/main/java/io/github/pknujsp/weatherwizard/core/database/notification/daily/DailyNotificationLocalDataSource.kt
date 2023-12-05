package io.github.pknujsp.weatherwizard.core.database.notification.daily

import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDto
import kotlinx.coroutines.flow.Flow

interface DailyNotificationLocalDataSource {
    fun getDailyNotifications(): Flow<List<NotificationDto>>
    suspend fun getDailyNotification(id: Long): NotificationDto
    suspend fun updateDailyNotification(notificationDto: NotificationDto): Boolean
    suspend fun removeDailyNotification(id: Long)

    suspend fun switch(id: Long, enabled: Boolean)
}