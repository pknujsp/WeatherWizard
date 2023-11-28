package io.github.pknujsp.weatherwizard.core.data.notification.daily

import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationSettingsEntity
import io.github.pknujsp.weatherwizard.core.database.notification.daily.DailyNotificationLocalDataSource
import io.github.pknujsp.weatherwizard.core.data.notification.daily.model.DailyNotificationSettingsJsonEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DailyNotificationRepositoryImpl @Inject constructor(
    private val dataSource: DailyNotificationLocalDataSource,
    @KtJson json: Json,
) : DailyNotificationRepository {

    private val jsonParser = JsonParser(json)
    override suspend fun switch(id: Long, enabled: Boolean) {
        dataSource.switch(id, enabled)
    }

    override fun getDailyNotifications(): Flow<List<NotificationSettingsEntity<DailyNotificationSettingsJsonEntity>>> = dataSource.getDailyNotifications()
        .map { list ->
            list.map{ dto ->
                val entity = dto
            }
        }

    override suspend fun getDailyNotification(id: Long): NotificationSettingsEntity<DailyNotificationSettingsJsonEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun updateDailyNotification(entity: NotificationSettingsEntity<DailyNotificationSettingsJsonEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDailyNotification(id: Long) {
        TODO("Not yet implemented")
    }


}