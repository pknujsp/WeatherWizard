package io.github.pknujsp.weatherwizard.core.database.widget

import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEmpty
import java.time.ZonedDateTime
import javax.inject.Inject

class WidgetLocalDataSourceImpl @Inject constructor(
    private val widgetDao: WidgetDao
) : WidgetLocalDataSource {

    override suspend fun add(widgetDto: WidgetDto): Int = widgetDao.insert(widgetDto).toInt()

    override fun getAll(): Flow<List<WidgetDto>> = widgetDao.getAll().onEmpty {
        emit(emptyList())
    }

    override suspend fun getById(id: Int): WidgetDto = widgetDao.getById(id)


    override suspend fun deleteById(id: Int) = widgetDao.deleteById(id)


    override suspend fun containsId(id: Int): Boolean = widgetDao.containsId(id)


    override suspend fun updateResponseData(id: Int, status: WidgetStatus, responseData: ByteArray) {
        widgetDao.updateResponseData(id, status, responseData, ZonedDateTime.now().toString())
    }
}