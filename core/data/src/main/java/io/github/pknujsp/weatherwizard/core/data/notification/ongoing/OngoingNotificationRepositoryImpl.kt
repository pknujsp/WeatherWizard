package io.github.pknujsp.weatherwizard.core.data.notification.ongoing

import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.model.OngoingNotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDto
import io.github.pknujsp.weatherwizard.core.database.notification.ongoing.OngoingNotificationLocalDataSource
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.model.OngoingNotificationSettingsJsonEntity
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import kotlinx.serialization.json.Json
import javax.inject.Inject

class OngoingNotificationRepositoryImpl @Inject constructor(
    private val ongoingNotificationLocalDataSource: OngoingNotificationLocalDataSource,
    @KtJson json: Json,
) : OngoingNotificationRepository {

    private val jsonParser = JsonParser(json)

    override suspend fun getOngoingNotification(): NotificationSettingsEntity<OngoingNotificationSettingsEntity> {
        val dto = ongoingNotificationLocalDataSource.getOngoingNotification()
        val entity = dto?.run {
            val jsonEntity = jsonParser.parse<OngoingNotificationSettingsJsonEntity>(content)

            OngoingNotificationSettingsEntity(
                notificationIconType = jsonEntity.getNotificationIconType(),
                refreshInterval = jsonEntity.getRefreshInterval(),
                weatherProvider = jsonEntity.getWeatherProvider(),
                location = jsonEntity.getLocation(),
            )
        } ?: OngoingNotificationSettingsEntity()

        return NotificationSettingsEntity(dto?.id ?: 0L, dto?.enabled ?: false, entity, dto != null)
    }

    override suspend fun updateOngoingNotification(notificationSettingsEntity: NotificationSettingsEntity<OngoingNotificationSettingsEntity>): Boolean {
        val entity = notificationSettingsEntity.data

        val jsonEntity = OngoingNotificationSettingsJsonEntity(
            notificationIconType = entity.notificationIconType.key,
            refreshInterval = entity.refreshInterval.key,
            weatherProvider = entity.weatherProvider.key,
            latitude = entity.location.latitude,
            longitude = entity.location.longitude,
            address = entity.location.address,
            country = entity.location.country,
            locationType = entity.location.locationType.key,
        )

        return ongoingNotificationLocalDataSource.updateOngoingNotification(NotificationDto(
            id = notificationSettingsEntity.id,
            notificationType = NotificationType.ONGOING.notificationId,
            enabled = notificationSettingsEntity.enabled,
            content = jsonParser.parse(jsonEntity),
        ))
    }

    override suspend fun switch(enabled: Boolean) {
        ongoingNotificationLocalDataSource.switch(enabled)
    }
}