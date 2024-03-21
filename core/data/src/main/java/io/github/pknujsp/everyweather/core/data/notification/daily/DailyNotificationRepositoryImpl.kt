package io.github.pknujsp.everyweather.core.data.notification.daily

import io.github.pknujsp.everyweather.core.common.NotificationType
import io.github.pknujsp.everyweather.core.data.mapper.JsonParser
import io.github.pknujsp.everyweather.core.data.notification.daily.model.DailyNotificationSettingsEntity
import io.github.pknujsp.everyweather.core.data.notification.daily.model.DailyNotificationSettingsJsonEntity
import io.github.pknujsp.everyweather.core.database.notification.NotificationDto
import io.github.pknujsp.everyweather.core.database.notification.daily.DailyNotificationLocalDataSource
import io.github.pknujsp.everyweather.core.model.notification.NotificationSettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DailyNotificationRepositoryImpl(
    private val dataSource: DailyNotificationLocalDataSource,
    private val jsonParser: JsonParser,
) : DailyNotificationRepository {
    override suspend fun switch(
        id: Long,
        enabled: Boolean,
    ) {
        dataSource.switch(id, enabled)
    }

    private fun NotificationDto.toSettingsEntity(): DailyNotificationSettingsEntity {
        val jsonEntity = jsonParser.parse<DailyNotificationSettingsJsonEntity>(content)
        return DailyNotificationSettingsEntity(
            type = jsonEntity.getType(),
            weatherProvider = jsonEntity.getWeatherProvider(),
            location = jsonEntity.getLocation(),
            hour = jsonEntity.hour,
            minute = jsonEntity.minute,
        )
    }

    override fun getDailyNotifications(): Flow<List<NotificationSettingsEntity<DailyNotificationSettingsEntity>>> =
        dataSource.getDailyNotifications().map { list ->
            list.map {
                NotificationSettingsEntity(
                    id = it.id,
                    enabled = it.enabled,
                    data = it.toSettingsEntity(),
                    isInitialized = true,
                )
            }
        }

    override suspend fun getDailyNotification(id: Long): NotificationSettingsEntity<DailyNotificationSettingsEntity> =
        dataSource.getDailyNotification(id).let {
            NotificationSettingsEntity(
                id = it.id,
                enabled = it.enabled,
                data = it.toSettingsEntity(),
                isInitialized = true,
            )
        }

    override suspend fun updateDailyNotification(entity: NotificationSettingsEntity<DailyNotificationSettingsEntity>) {
        entity.run {
            val encoded = toJsonEntity()
            dataSource.updateDailyNotification(
                NotificationDto(
                    id = id,
                    enabled = enabled,
                    notificationType = NotificationType.DAILY.notificationId,
                    content = encoded,
                ),
            )
        }
    }

    override suspend fun createDailyNotification(entity: NotificationSettingsEntity<DailyNotificationSettingsEntity>): Long =
        entity.run {
            val encoded = toJsonEntity()
            dataSource.updateDailyNotification(
                NotificationDto(
                    id = id,
                    enabled = enabled,
                    notificationType = NotificationType.DAILY.notificationId,
                    content = encoded,
                ),
            )
        }

    override suspend fun deleteDailyNotification(id: Long) {
        dataSource.removeDailyNotification(id)
    }

    private fun NotificationSettingsEntity<DailyNotificationSettingsEntity>.toJsonEntity() =
        jsonParser.parse(
            DailyNotificationSettingsJsonEntity(
                latitude = data.location.latitude,
                longitude = data.location.longitude,
                address = data.location.address,
                country = data.location.country,
                hour = data.hour,
                minute = data.minute,
                locationType = data.location.locationType.key,
                weatherProvider = data.weatherProvider.key,
                type = data.type.key,
            ),
        )
}
