package io.github.pknujsp.weatherwizard.core.data.widget

import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.database.widget.WidgetDto
import io.github.pknujsp.weatherwizard.core.database.widget.WidgetLocalDataSource
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.time.ZonedDateTime
import javax.inject.Inject

class WidgetRepositoryImpl @Inject constructor(
    private val dataSource: WidgetLocalDataSource,
    @KtJson json: Json,
) : WidgetRepository {

    private val jsonParser: JsonParser = JsonParser(json)

    override fun getAll(): Flow<WidgetSettingsEntityList> = dataSource.getAll().map {
        val list = it.map { dto ->
            val jsonEntity = jsonParser.parse<WidgetSettingsJsonEntity>(dto.content)
            WidgetSettingsEntity(id = dto.id,
                location = jsonEntity.getLocation(),
                weatherProvider = jsonEntity.getWeatherProvider(),
                widgetType = WidgetType.fromKey(dto.widgetType))
        }

        WidgetSettingsEntityList(list)
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun add(entity: WidgetSettingsEntity): Int {
        val jsonEntity = WidgetSettingsJsonEntity(
            weatherProvider = entity.weatherProvider.key,
            locationType = entity.location.locationType.key,
            latitude = entity.location.latitude,
            longitude = entity.location.longitude,
            address = entity.location.address,
            country = entity.location.country,
        )

        val encoded = jsonParser.parse(jsonEntity)
        return dataSource.add(WidgetDto(id = entity.id, widgetType = entity.widgetType.key, content = encoded))
    }

    override suspend fun get(id: Int): WidgetSettingsEntity {
        val dto = dataSource.getById(id)
        val jsonEntity = jsonParser.parse<WidgetSettingsJsonEntity>(dto.content)

        return WidgetSettingsEntity(id = dto.id,
            location = jsonEntity.getLocation(),
            weatherProvider = jsonEntity.getWeatherProvider(),
            widgetType = WidgetType.fromKey(dto.widgetType))
    }

    override suspend fun delete(id: Int) {
        dataSource.deleteById(id)
    }

    override suspend fun updateResponseData(id: Int, status: WidgetStatus, responseData: ByteArray) {
        dataSource.updateResponseData(id, status, responseData)
    }

    override suspend fun get(widgetIds: Array<Int>?, all: Boolean): List<WidgetResponseDBEntity> {
        val dtoList = if (all || widgetIds == null) {
            dataSource.getAll().first()
        } else {
            widgetIds.map { dataSource.getById(it) }
        }

        val dbEntities = dtoList.map { dto ->
            val jsonEntity = jsonParser.parse<WidgetSettingsJsonEntity>(dto.content)
            val responseData = jsonParser.parse<WidgetResponseDBModel>(dto.responseData)

            WidgetResponseDBEntity(id = dto.id,
                weatherProvider = jsonEntity.getWeatherProvider(),
                widgetType = WidgetType.fromKey(dto.widgetType),
                address = responseData.address,
                latitude = responseData.latitude,
                longitude = responseData.longitude,
                updatedAt = ZonedDateTime.parse(dto.updatedAt),
                status = dto.status,
                entities = responseData.entities.map {
                    it.toWeatherEntity(jsonParser)
                })
        }
        return dbEntities
    }
}