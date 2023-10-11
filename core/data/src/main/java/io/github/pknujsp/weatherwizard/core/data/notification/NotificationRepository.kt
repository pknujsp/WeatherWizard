package io.github.pknujsp.weatherwizard.core.data.notification

import io.github.pknujsp.weatherwizard.core.model.notification.OngoingNotificationInfoEntity

interface NotificationRepository {


    suspend fun getOngoingNotificationInfo(): Result<OngoingNotificationInfoEntity>
    suspend fun setOngoingNotificationInfo(ongoingNotificationInfoEntity: OngoingNotificationInfoEntity) : Long


}