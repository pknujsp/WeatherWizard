package io.github.pknujsp.weatherwizard.core.data.widget

import io.github.pknujsp.weatherwizard.core.model.widget.WidgetEntity
import kotlinx.coroutines.flow.Flow

interface WidgetRepository {

    fun getAll(): Flow<List<WidgetEntity>>
    suspend fun get(id: Int): WidgetEntity

    suspend fun add(entity: WidgetEntity): Int

    suspend fun delete(id: Int)
}