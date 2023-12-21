package io.github.pknujsp.weatherwizard.core.data.widget

import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus
import kotlinx.coroutines.flow.Flow

interface WidgetRepository {

    fun getAll(): Flow<WidgetSettingsEntityList>

    suspend fun get(widgetIds: Array<Int>?, all: Boolean): List<WidgetResponseDBEntity>

    suspend fun get(id: Int): WidgetSettingsEntity

    suspend fun add(entity: WidgetSettingsEntity): Int

    suspend fun delete(id: Int)

    suspend fun updateResponseData(id: Int, status: WidgetStatus, responseData: ByteArray)
}