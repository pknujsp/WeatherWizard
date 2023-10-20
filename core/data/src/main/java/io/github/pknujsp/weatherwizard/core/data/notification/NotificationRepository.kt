package io.github.pknujsp.weatherwizard.core.data.notification

import io.github.pknujsp.weatherwizard.core.model.notification.NotificationEntity
import io.github.pknujsp.weatherwizard.core.model.notification.ongoing.OngoingNotificationInfoEntity
import io.github.pknujsp.weatherwizard.core.model.notification.daily.DailyNotificationInfoEntity
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun switch(id: Long, enabled: Boolean)

    suspend fun getOngoingNotification(): NotificationEntity<OngoingNotificationInfoEntity>
    suspend fun setOngoingNotificationInfo(ongoingNotificationInfoEntity: NotificationEntity<OngoingNotificationInfoEntity>): Long

    fun getDailyNotifications(): Flow<List<NotificationEntity<DailyNotificationInfoEntity>>>
    suspend fun getDailyNotification(id:Long): NotificationEntity<DailyNotificationInfoEntity>

    suspend fun setDailyNotificationInfo(entity: NotificationEntity<DailyNotificationInfoEntity>): Long

    suspend fun deleteDailyNotification(id: Long)
}