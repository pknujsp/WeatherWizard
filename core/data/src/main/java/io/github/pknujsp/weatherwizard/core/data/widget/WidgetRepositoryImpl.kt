package io.github.pknujsp.weatherwizard.core.data.widget

import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDto
import io.github.pknujsp.weatherwizard.core.database.notification.NotificationLocalDataSource
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationEntity
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import io.github.pknujsp.weatherwizard.core.model.notification.ongoing.OngoingNotificationInfoEntity
import io.github.pknujsp.weatherwizard.core.model.notification.daily.DailyNotificationInfoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WidgetRepositoryImpl @Inject constructor(
    private val notificationLocalDataSource: NotificationLocalDataSource,
    @KtJson json: Json,
) : WidgetRepository {

    private val jsonParser: JsonParser = JsonParser(json)

    override suspend fun getOngoingNotification() =
        notificationLocalDataSource.getAll(NotificationType.ONGOING).first().let {
            if (it.isEmpty()) {
                NotificationEntity(-1L, false, OngoingNotificationInfoEntity())
            } else {
                val dto = it[0]
                val entity = jsonParser.parse<OngoingNotificationInfoEntity>(dto.content)
                NotificationEntity(dto.id, dto.enabled, entity)
            }
        }

    override suspend fun switch(id: Long, enabled: Boolean) {
        notificationLocalDataSource.switch(id, enabled)
    }


    override suspend fun setOngoingNotificationInfo(ongoingNotificationInfoEntity: NotificationEntity<OngoingNotificationInfoEntity>): Long {
        return notificationLocalDataSource.updateNotification(
            NotificationDto(
                id = ongoingNotificationInfoEntity.idInDb,
                notificationType = NotificationType.ONGOING.notificationId,
                enabled = ongoingNotificationInfoEntity.enabled,
                content = jsonParser.parse(ongoingNotificationInfoEntity.data),
            )
        )
    }

    override fun getDailyNotifications(): Flow<List<NotificationEntity<DailyNotificationInfoEntity>>> =
        notificationLocalDataSource.getAll(NotificationType.DAILY).map { list ->
            list.map {
                NotificationEntity(it.id, it.enabled, jsonParser.parse(it.content))
            }
        }

    override suspend fun getDailyNotification(id: Long): NotificationEntity<DailyNotificationInfoEntity> =
        if(id == -1L){
            NotificationEntity(-1L, true, DailyNotificationInfoEntity())
        }else{
            notificationLocalDataSource.getById(id).let {
                NotificationEntity(it.id, it.enabled, jsonParser.parse(it.content))
            }
        }
    override suspend fun setDailyNotificationInfo(entity: NotificationEntity<DailyNotificationInfoEntity>): Long {
        return notificationLocalDataSource.updateNotification(
            NotificationDto(
                id = entity.idInDb,
                notificationType = NotificationType.DAILY.notificationId,
                enabled = entity.enabled,
                content = jsonParser.parse(entity.data),
            )
        )
    }

    override suspend fun deleteDailyNotification(id: Long) {
        notificationLocalDataSource.deleteById(id)
    }
}