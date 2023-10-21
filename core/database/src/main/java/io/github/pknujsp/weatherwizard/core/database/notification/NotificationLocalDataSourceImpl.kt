package io.github.pknujsp.weatherwizard.core.database.notification

import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationLocalDataSourceImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationLocalDataSource {
    override suspend fun switch(id: Long, enabled: Boolean) {
        notificationDao.switchState(id, enabled)
    }

    override suspend fun updateNotification(searchHistoryDto: NotificationDto): Long {
        return notificationDao.insert(searchHistoryDto)
    }

    override fun getAll(): Flow<List<NotificationDto>> {
        return notificationDao.getAll()
    }

    override fun getAll(notificationType: NotificationType): Flow<List<NotificationDto>> {
        return notificationDao.getAll(notificationType.notificationId)
    }

    override suspend fun getById(id: Long): NotificationDto {
        return notificationDao.getById(id)
    }

    override suspend fun deleteById(id: Long) {
        return notificationDao.deleteById(id)
    }

    override suspend fun containsId(id: Long): Boolean {
        return notificationDao.containsId(id)
    }

    override suspend fun containsNotificationType(notificationType: NotificationType): Boolean {
        return notificationDao.containsNotificationTypeId(notificationType.notificationId)
    }

}