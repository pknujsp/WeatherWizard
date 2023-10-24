package io.github.pknujsp.weatherwizard.core.database.widget

import io.github.pknujsp.weatherwizard.core.database.notification.NotificationDto
import io.github.pknujsp.weatherwizard.core.model.notification.enums.NotificationType
import kotlinx.coroutines.flow.Flow

interface WidgetLocalDataSource {

    suspend fun add(widgetDto: WidgetDto): Int

    fun getAll(): Flow<List<WidgetDto>>

    suspend fun getById(id: Int): WidgetDto

    suspend fun deleteById(id: Int)

    suspend fun containsId(id: Int): Boolean

}