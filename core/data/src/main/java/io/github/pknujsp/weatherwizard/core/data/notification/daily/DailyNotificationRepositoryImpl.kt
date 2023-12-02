package io.github.pknujsp.weatherwizard.core.data.notification.daily

import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.data.notification.daily.model.DailyNotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.database.notification.daily.DailyNotificationLocalDataSource
import io.github.pknujsp.weatherwizard.core.data.notification.daily.model.DailyNotificationSettingsJsonEntity
import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDto
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull.content
import javax.inject.Inject

class DailyNotificationRepositoryImpl @Inject constructor(
    private val dataSource: DailyNotificationLocalDataSource,
    @KtJson json: Json,
) : DailyNotificationRepository {

    private val jsonParser = JsonParser(json)
    override suspend fun switch(id: Long, enabled: Boolean) {
        dataSource.switch(id, enabled)
    }

    private fun NotificationDto.toSettingsEntity(): DailyNotificationSettingsEntity {
        val jsonEntity = jsonParser.parse<DailyNotificationSettingsJsonEntity>(content)
        val locationType = jsonEntity.getLocationType()

        return DailyNotificationSettingsEntity(
            type = jsonEntity.getType(),
            weatherProvider = jsonEntity.getWeatherProvider(),
            locationType = locationType,
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
            val locationType = data.locationType
            val (latitude, longitude, addressName) = when (locationType) {
                is LocationType.CustomLocation -> Triple(locationType.latitude, locationType.longitude, locationType.address)
                else -> Triple(0.0, 0.0, "")
            }

            val content = jsonParser.parse(DailyNotificationSettingsJsonEntity(
                latitude = latitude,
                longitude = longitude,
                addressName = addressName,
                hour = data.hour,
                minute = data.minute,
                locationType = locationType.key,
                weatherProvider = data.weatherProvider.key,
                type = data.type.key,
            ))

            dataSource.updateDailyNotification(NotificationDto(
                id = id,
                enabled = enabled,
                notificationType = NotificationType.DAILY.notificationId,
                content = content,
            ))
        }
    }

    override suspend fun deleteDailyNotification(id: Long) {
        dataSource.removeDailyNotification(id)
    }

}