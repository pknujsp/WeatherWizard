package io.github.pknujsp.weatherwizard.core.database.notification

import io.github.pknujsp.weatherwizard.core.model.notification.NotificationType
import kotlinx.coroutines.flow.Flow

interface NotificationLocalDataSource {

    suspend fun switch(id: Long, enabled: Boolean)
    suspend fun updateNotification(searchHistoryDto: NotificationDto): Long

    fun getAll(): Flow<List<NotificationDto>>

    fun getAll(notificationType: NotificationType): Flow<List<NotificationDto>>
    suspend fun getById(id: Long): NotificationDto

    suspend fun deleteById(id: Long)

    suspend fun containsId(id: Long): Boolean

    suspend fun containsNotificationType(notificationType: NotificationType): Boolean
}