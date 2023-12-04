package io.github.pknujsp.weatherwizard.core.data.widget

import kotlinx.coroutines.flow.Flow

interface WidgetRepository {

    fun getAll(): Flow<WidgetSettingsEntityList>
    suspend fun get(id: Int): WidgetSettingsEntity

    suspend fun add(entity: WidgetSettingsEntity): Int

    suspend fun delete(id: Int)
}