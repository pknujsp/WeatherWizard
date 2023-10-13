package io.github.pknujsp.weatherwizard.core.database.notification

import kotlinx.coroutines.flow.Flow

interface NotificationLocalDataSource {
    suspend fun insert(searchHistoryDto: NotificationDto): Long

    fun getAll(): Flow<List<NotificationDto>>

    fun getAll(notificationTypeId: Int): Flow<List<NotificationDto>>
    suspend fun getById(id: Long): NotificationDto

    suspend fun deleteById(id: Long)

    suspend fun containsId(id: Long): Boolean

    suspend fun containsNotificationTypeId(notificationTypeId: Int): Boolean
}