package io.github.pknujsp.weatherwizard.core.data.notification.ongoing

import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.model.OngoingNotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDto
import io.github.pknujsp.weatherwizard.core.database.notification.ongoing.OngoingNotificationLocalDataSource
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import io.github.pknujsp.weatherwizard.core.data.notification.ongoing.model.OngoingNotificationSettingsJsonEntity
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
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

            val locationType = jsonEntity.getLocationType()
            val (latitude, longitude, addressName) = if (locationType is LocationType.CustomLocation) {
                Triple(locationType.latitude, locationType.longitude, locationType.address)
            } else {
                Triple(0.0, 0.0, "")
            }

            OngoingNotificationSettingsEntity(notificationIconType = jsonEntity.getNotificationIconType(),
                refreshInterval = jsonEntity.getRefreshInterval(),
                weatherProvider = jsonEntity.getWeatherProvider(),
                latitude = latitude,
                longitude = longitude,
                addressName = addressName,
                locationType = locationType)
        } ?: OngoingNotificationSettingsEntity()

        return NotificationSettingsEntity(dto?.id ?: 0L, dto?.enabled ?: false, entity, dto != null)
    }

    override suspend fun updateOngoingNotification(notificationSettingsEntity: NotificationSettingsEntity<OngoingNotificationSettingsEntity>): Boolean {
        val entity = notificationSettingsEntity.data
        val locationType = entity.locationType
        val (latitude, longitude, addressName) = if (locationType is LocationType.CustomLocation) {
            Triple(locationType.latitude, locationType.longitude, locationType.address)
        } else {
            Triple(0.0, 0.0, "")
        }

        val jsonEntity = OngoingNotificationSettingsJsonEntity(
            notificationIconType = entity.notificationIconType.key,
            refreshInterval = entity.refreshInterval.key,
            weatherProvider = entity.weatherProvider.key,
            latitude = latitude,
            longitude = longitude,
            addressName = addressName,
            locationType = locationType.key,
        )

        return ongoingNotificationLocalDataSource.updateOngoingNotification(NotificationDto(
            id = notificationSettingsEntity.id,
            notificationType = NotificationType.ONGOING.notificationId,
            enabled = notificationSettingsEntity.enabled,
            content = jsonParser.parse(jsonEntity),
        ))
    }
}