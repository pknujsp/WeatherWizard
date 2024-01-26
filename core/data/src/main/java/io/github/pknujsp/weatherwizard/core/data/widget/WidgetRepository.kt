package io.github.pknujsp.weatherwizard.core.data.widget

import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus

interface WidgetRepository {

    suspend fun getAll(): WidgetSettingsEntityList

    suspend fun get(widgetIds: List<Int>): List<SavedWidgetContentState>

    suspend fun get(id: Int): WidgetSettingsEntity?

    suspend fun add(entity: WidgetSettingsEntity): Int

    suspend fun delete(id: Int)

    suspend fun deleteAll()

    suspend fun updateResponseData(id: Int, status: WidgetStatus, responseData: ByteArray?)
}