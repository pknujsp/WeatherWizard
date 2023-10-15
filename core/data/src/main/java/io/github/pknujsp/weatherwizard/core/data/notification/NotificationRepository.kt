package io.github.pknujsp.weatherwizard.core.data.notification

import io.github.pknujsp.weatherwizard.core.model.notification.NotificationEntity
import io.github.pknujsp.weatherwizard.core.model.notification.OngoingNotificationInfoEntity
import io.github.pknujsp.weatherwizard.core.model.notification.daily.DailyNotificationInfoEntity
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {


    suspend fun getOngoingNotificationInfo(): NotificationEntity<OngoingNotificationInfoEntity>
    suspend fun setOngoingNotificationInfo(ongoingNotificationInfoEntity: NotificationEntity<OngoingNotificationInfoEntity>) : Long

    fun getDailyNotificationInfo(): Flow<List<NotificationEntity<DailyNotificationInfoEntity>>>
}