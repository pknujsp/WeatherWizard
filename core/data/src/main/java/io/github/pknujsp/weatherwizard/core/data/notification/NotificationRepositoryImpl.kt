package io.github.pknujsp.weatherwizard.core.data.notification

import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDto
import io.github.pknujsp.weatherwizard.core.database.notification.NotificationLocalDataSource
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationEntity
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationType
import io.github.pknujsp.weatherwizard.core.model.notification.OngoingNotificationInfoEntity
import io.github.pknujsp.weatherwizard.core.model.notification.daily.DailyNotificationInfoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationLocalDataSource: NotificationLocalDataSource,
    @KtJson json: Json,
) : NotificationRepository {

    private val jsonParser: JsonParser = JsonParser(json)

    override suspend fun getOngoingNotificationInfo() =
        notificationLocalDataSource.getAll(NotificationType.ONGOING.notificationId).first().let {
            if (it.isEmpty()) {
                NotificationEntity(-1L, false, OngoingNotificationInfoEntity())
            } else {
                val dto = it[0]
                val entity = jsonParser.parse<OngoingNotificationInfoEntity>(dto.content)
                NotificationEntity(dto.id, dto.enabled, entity)
            }
        }


    override suspend fun setOngoingNotificationInfo(ongoingNotificationInfoEntity: NotificationEntity<OngoingNotificationInfoEntity>): Long {
        return notificationLocalDataSource.insert(
            NotificationDto(
                id = ongoingNotificationInfoEntity.idInDb,
                notificationType = NotificationType.ONGOING.notificationId,
                enabled = ongoingNotificationInfoEntity.enabled,
                content = jsonParser.parse(ongoingNotificationInfoEntity.data),
            )
        )
    }

    override fun getDailyNotificationInfo(): Flow<List<NotificationEntity<DailyNotificationInfoEntity>>> {
        TODO("Not yet implemented")
    }
}