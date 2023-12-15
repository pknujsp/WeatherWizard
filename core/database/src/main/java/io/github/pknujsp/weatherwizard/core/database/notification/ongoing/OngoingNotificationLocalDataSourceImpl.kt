package io.github.pknujsp.weatherwizard.core.database.notification.ongoing

import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDao
import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDto
import io.github.pknujsp.weatherwizard.core.common.manager.NotificationType
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class OngoingNotificationLocalDataSourceImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : OngoingNotificationLocalDataSource {

    override suspend fun getOngoingNotification(): NotificationDto? =
        notificationDao.getAll(NotificationType.ONGOING.notificationId).firstOrNull()?.firstOrNull()

    override suspend fun updateOngoingNotification(notificationDto: NotificationDto): Boolean = notificationDao.insert(notificationDto) >= 0

    override suspend fun switch(enabled: Boolean) {
        notificationDao.switchOngointNotificationState(enabled = enabled)
    }
}