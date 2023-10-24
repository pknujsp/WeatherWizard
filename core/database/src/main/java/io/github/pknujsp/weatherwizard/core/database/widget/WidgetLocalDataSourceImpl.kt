package io.github.pknujsp.weatherwizard.core.database.widget

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WidgetLocalDataSourceImpl @Inject constructor(
    private val widgetDao: WidgetDao
) : WidgetLocalDataSource {
    override suspend fun add(widgetDto: WidgetDto): Int {
        return widgetDao.insert(widgetDto).toInt()
    }

    override fun getAll(): Flow<List<WidgetDto>> {
        return widgetDao.getAll()
    }

    override suspend fun getById(id: Int): WidgetDto {
        return widgetDao.getById(id)
    }

    override suspend fun deleteById(id: Int) {
        return widgetDao.deleteById(id)
    }

    override suspend fun containsId(id: Int): Boolean {
        return widgetDao.containsId(id)
    }
}