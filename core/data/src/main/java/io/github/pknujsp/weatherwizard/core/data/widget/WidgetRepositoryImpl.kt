package io.github.pknujsp.weatherwizard.core.data.widget

import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.database.widget.WidgetDto
import io.github.pknujsp.weatherwizard.core.database.widget.WidgetLocalDataSource
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
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
    override fun getAll(): Flow<WidgetSettingsEntityList> = dataSource.getAll().map {
        val list = it.map { dto ->
            val jsonEntity = jsonParser.parse<WidgetSettingsJsonEntity>(dto.content)
            WidgetSettingsEntity(
                id = dto.id,
                locationType = jsonEntity.getLocationType(),
                weatherProvider = jsonEntity.getWeatherProvider(),
                widgetType = WidgetType.fromKey(dto.widgetType)
            )
        }

        WidgetSettingsEntityList(list)
    }

    override suspend fun add(entity: WidgetSettingsEntity): Int {
        val jsonEntity = WidgetSettingsJsonEntity(
            latitude = if (entity.locationType is LocationType.CustomLocation) entity.locationType.latitude else 0.0,
            longitude = if (entity.locationType is LocationType.CustomLocation) entity.locationType.longitude else 0.0,
            addressName = if (entity.locationType is LocationType.CustomLocation) entity.locationType.address else "",
            locationTypeKey = entity.locationType.key,
            weatherProviderKey = entity.weatherProvider.key,
        )

        return dataSource.add(WidgetDto(id = entity.id, widgetType = entity.widgetType.key, content = jsonParser.parse(jsonEntity)))
    }

    override suspend fun get(id: Int): WidgetSettingsEntity {
        val dto = dataSource.getById(id)
        val jsonEntity = jsonParser.parse<WidgetSettingsJsonEntity>(dto.content)

        return WidgetSettingsEntity(
            id = dto.id,
            locationType = jsonEntity.getLocationType(),
            weatherProvider = jsonEntity.getWeatherProvider(),
            widgetType = WidgetType.fromKey(dto.widgetType)
        )
    }

    override suspend fun delete(id: Int) {
        dataSource.deleteById(id)
    }
}