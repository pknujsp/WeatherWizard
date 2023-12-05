package io.github.pknujsp.weatherwizard.core.database.notification.daily

import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDao
import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDto
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DailyNotificationLocalDataSourceImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : DailyNotificationLocalDataSource {
    override fun getDailyNotifications(): Flow<List<NotificationDto>> = notificationDao.getAll(NotificationType.DAILY.notificationId)

    override suspend fun getDailyNotification(id: Long): NotificationDto = notificationDao.getById(id)

    override suspend fun updateDailyNotification(notificationDto: NotificationDto): Boolean = notificationDao.insert(notificationDto) >= 0

    override suspend fun removeDailyNotification(id: Long) = notificationDao.deleteById(id)

    override suspend fun switch(id: Long, enabled: Boolean) {
        notificationDao.switchState(id, enabled)
    }

}