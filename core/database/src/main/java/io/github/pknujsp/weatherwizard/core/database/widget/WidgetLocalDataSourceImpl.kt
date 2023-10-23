package io.github.pknujsp.weatherwizard.core.database.widget

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WidgetLocalDataSourceImpl @Inject constructor(
    private val widgetDao: WidgetDao
) : WidgetLocalDataSource {
    override suspend fun add(widgetDto: WidgetDto): Long {
        return widgetDao.insert(widgetDto)
    }

    override fun getAll(): Flow<List<WidgetDto>> {
        return widgetDao.getAll()
    }

    override suspend fun getById(id: Long): WidgetDto {
        return widgetDao.getById(id)
    }

    override suspend fun deleteById(id: Long) {
        return widgetDao.deleteById(id)
    }

    override suspend fun containsId(id: Long): Boolean {
        return widgetDao.containsId(id)
    }
}