package io.github.pknujsp.weatherwizard.core.database.notification.daily

import io.github.pknujsp.weatherwizard.core.common.NotificationType
import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDao
import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEmpty
import javax.inject.Inject

class DailyNotificationLocalDataSourceImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : DailyNotificationLocalDataSource {
    override fun getDailyNotifications(): Flow<List<NotificationDto>> =
        notificationDao.getAll(NotificationType.DAILY.notificationId).filterNotNull().onEmpty { emit(emptyList()) }

    override suspend fun getDailyNotification(id: Long): NotificationDto = notificationDao.getById(id)

    override suspend fun updateDailyNotification(notificationDto: NotificationDto): Long = notificationDao.insert(notificationDto)

    override suspend fun removeDailyNotification(id: Long) = notificationDao.deleteById(id)

    override suspend fun switch(id: Long, enabled: Boolean) {
        notificationDao.switchState(id, enabled)
    }

}