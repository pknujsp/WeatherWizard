package io.github.pknujsp.weatherwizard.core.data.widget

import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.database.widget.WidgetDto
import io.github.pknujsp.weatherwizard.core.database.widget.WidgetLocalDataSource
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetEntity
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

class WidgetRepositoryImpl @Inject constructor(
    private val dataSource: WidgetLocalDataSource,
    @KtJson json: Json,
) : WidgetRepository {

    private val jsonParser: JsonParser = JsonParser(json)
    override fun getAll(): Flow<List<WidgetEntity>> = dataSource.getAll().map {
        it.map { dto ->
            WidgetEntity(id = dto.id, content = jsonParser.parse(dto.content), widgetType = WidgetType.fromOrdinal(dto.widgetType))
        }
    }

    override suspend fun add(entity: WidgetEntity): Int {
        return dataSource.add(WidgetDto(id = entity.id, widgetType = entity.widgetType.ordinal, content = jsonParser.parse(entity.content)))
    }

    override suspend fun get(id: Int): WidgetEntity {
        return WidgetEntity(id = id,
            content = jsonParser.parse(dataSource.getById(id).content),
            widgetType = WidgetType.fromOrdinal(dataSource.getById(id).widgetType))
    }

    override suspend fun delete(id: Int) {
        dataSource.deleteById(id)
    }
}