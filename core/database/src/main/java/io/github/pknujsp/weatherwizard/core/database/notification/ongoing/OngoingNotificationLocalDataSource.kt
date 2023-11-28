package io.github.pknujsp.weatherwizard.core.database.notification.ongoing

import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDto

interface OngoingNotificationLocalDataSource {
    suspend fun getOngoingNotification(): NotificationDto?
    suspend fun updateOngoingNotification(notificationDto: NotificationDto): Boolean
}