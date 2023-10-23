package io.github.pknujsp.weatherwizard.core.data.widget

import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.notification.NotificationEntity
import io.github.pknujsp.weatherwizard.core.model.notification.ongoing.OngoingNotificationInfoEntity
import io.github.pknujsp.weatherwizard.core.model.notification.daily.DailyNotificationInfoEntity
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetEntity
import kotlinx.coroutines.flow.Flow

interface WidgetRepository {

    fun getAll(): Flow<List<WidgetEntity>>
    suspend fun get(id: Long): WidgetEntity

    suspend fun add(entity: WidgetEntity): Long

    suspend fun delete(id: Long)
}