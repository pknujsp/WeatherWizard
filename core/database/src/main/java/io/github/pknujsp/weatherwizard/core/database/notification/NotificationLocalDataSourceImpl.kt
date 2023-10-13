package io.github.pknujsp.weatherwizard.core.database.notification

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotificationLocalDataSourceImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationLocalDataSource {
    override suspend fun insert(searchHistoryDto: NotificationDto): Long {
        return notificationDao.insert(searchHistoryDto)
    }

    override fun getAll(): Flow<List<NotificationDto>> {
        return notificationDao.getAll()
    }

    override fun getAll(notificationTypeId: Int): Flow<List<NotificationDto>> {
        return notificationDao.getAll(notificationTypeId)
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

    override suspend fun containsNotificationTypeId(notificationTypeId: Int): Boolean {
        return notificationDao.containsNotificationTypeId(notificationTypeId)
    }

}