package io.github.pknujsp.weatherwizard.core.database.widget

import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus

interface WidgetLocalDataSource {

    suspend fun add(widgetDto: WidgetDto): Int

    suspend fun getAll(excludesResponseData: Boolean): List<WidgetDto>

    suspend fun getById(id: Int): WidgetDto?

    suspend fun deleteById(id: Int)

    suspend fun containsId(id: Int): Boolean

    suspend fun updateResponseData(id: Int, status: WidgetStatus, responseData: ByteArray?)
}