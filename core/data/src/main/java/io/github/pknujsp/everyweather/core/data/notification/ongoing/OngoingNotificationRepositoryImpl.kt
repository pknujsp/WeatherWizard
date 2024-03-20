package io.github.pknujsp.everyweather.core.data.notification.ongoing

import io.github.pknujsp.everyweather.core.common.NotificationType
import io.github.pknujsp.everyweather.core.data.mapper.JsonParser
import io.github.pknujsp.everyweather.core.data.notification.ongoing.model.OngoingNotificationSettingsEntity
import io.github.pknujsp.everyweather.core.data.notification.ongoing.model.OngoingNotificationSettingsJsonEntity
import io.github.pknujsp.everyweather.core.database.notification.NotificationDto
import io.github.pknujsp.everyweather.core.database.notification.ongoing.OngoingNotificationLocalDataSource
import io.github.pknujsp.everyweather.core.model.notification.NotificationSettingsEntity

class OngoingNotificationRepositoryImpl(
    private val ongoingNotificationLocalDataSource: OngoingNotificationLocalDataSource,
    private val jsonParser: JsonParser,
) : OngoingNotificationRepository {
    override suspend fun getOngoingNotification(): NotificationSettingsEntity<OngoingNotificationSettingsEntity> {
        val dto = ongoingNotificationLocalDataSource.getOngoingNotification()
        val entity =
            dto?.run {
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

    override suspend fun updateOngoingNotification(
        notificationSettingsEntity: NotificationSettingsEntity<OngoingNotificationSettingsEntity>,
    ): Boolean {
        val entity = notificationSettingsEntity.data

        val jsonEntity =
            OngoingNotificationSettingsJsonEntity(
                notificationIconType = entity.notificationIconType.key,
                refreshInterval = entity.refreshInterval.key,
                weatherProvider = entity.weatherProvider.key,
                latitude = entity.location.latitude,
                longitude = entity.location.longitude,
                address = entity.location.address,
                country = entity.location.country,
                locationType = entity.location.locationType.key,
            )
        val encoded = jsonParser.parse(jsonEntity)
        return ongoingNotificationLocalDataSource.updateOngoingNotification(
            NotificationDto(
                id = notificationSettingsEntity.id,
                notificationType = NotificationType.ONGOING.notificationId,
                enabled = notificationSettingsEntity.enabled,
                content = encoded,
            ),
        )
    }

    override suspend fun switch(enabled: Boolean) {
        ongoingNotificationLocalDataSource.switch(enabled)
    }
}
