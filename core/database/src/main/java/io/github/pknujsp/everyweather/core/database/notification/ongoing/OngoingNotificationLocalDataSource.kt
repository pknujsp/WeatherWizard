package io.github.pknujsp.everyweather.core.database.notification.ongoing

import io.github.pknujsp.everyweather.core.database.notification.NotificationDto

interface OngoingNotificationLocalDataSource {
    suspend fun getOngoingNotification(): NotificationDto?

    suspend fun updateOngoingNotification(notificationDto: NotificationDto): Boolean

    suspend fun switch(enabled: Boolean)
}
