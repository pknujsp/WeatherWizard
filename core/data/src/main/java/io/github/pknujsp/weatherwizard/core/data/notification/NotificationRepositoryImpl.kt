package io.github.pknujsp.weatherwizard.core.data.notification

import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDto
import io.github.pknujsp.weatherwizard.core.database.notification.NotificationLocalDataSource
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationInfoEntityParser
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationType
import io.github.pknujsp.weatherwizard.core.model.notification.OngoingNotificationInfoEntity
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationLocalDataSource: NotificationLocalDataSource,
    @KtJson json: Json,
) : NotificationRepository {

    private val entityParser: NotificationInfoEntityParser = NotificationInfoEntityParser(json)

    override suspend fun getOngoingNotificationInfo(): Result<OngoingNotificationInfoEntity> {
        return notificationLocalDataSource.getAll(NotificationType.ONGOING.notificationId).firstOrNull()?.let {
            val dto = it[0]
            val entity = entityParser.parse<OngoingNotificationInfoEntity>(dto.content)
            Result.success(entity)
        } ?: Result.failure(Exception("No ongoing notification info"))
    }

    override suspend fun setOngoingNotificationInfo(ongoingNotificationInfoEntity: OngoingNotificationInfoEntity): Long {
        return notificationLocalDataSource.insert(
            NotificationDto(
                notificationTypeId = NotificationType.ONGOING.notificationId,
                content = entityParser.parse(ongoingNotificationInfoEntity),
            )
        )
    }
}