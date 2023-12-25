package io.github.pknujsp.weatherwizard.core.data.widget

import io.github.pknujsp.weatherwizard.core.common.module.KtJson
import io.github.pknujsp.weatherwizard.core.database.widget.WidgetDto
import io.github.pknujsp.weatherwizard.core.database.widget.WidgetLocalDataSource
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime
import javax.inject.Inject

class WidgetRepositoryImpl @Inject constructor(
    private val dataSource: WidgetLocalDataSource,
    @KtJson json: Json,
) : WidgetRepository {

    private val jsonParser: JsonParser = JsonParser(json)

    override suspend fun getAll(): WidgetSettingsEntityList = dataSource.getAll(true).let {
        val list = it.map { dto ->
            val jsonEntity = jsonParser.parse<WidgetSettingsJsonEntity>(dto.content)
            WidgetSettingsEntity(id = dto.id,
                location = jsonEntity.getLocation(),
                weatherProvider = jsonEntity.getWeatherProvider(),
                status = dto.status,
                widgetType = WidgetType.fromKey(dto.widgetType))
        }

        WidgetSettingsEntityList(list)
    }

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

    override suspend fun get(id: Int): WidgetSettingsEntity? {
        val dto = dataSource.getById(id) ?: return null
        val jsonEntity = jsonParser.parse<WidgetSettingsJsonEntity>(dto.content)

        return WidgetSettingsEntity(id = dto.id,
            location = jsonEntity.getLocation(),
            status = dto.status,
            weatherProvider = jsonEntity.getWeatherProvider(),
            widgetType = WidgetType.fromKey(dto.widgetType))
    }

    override suspend fun delete(id: Int) {
        dataSource.deleteById(id)
    }

    override suspend fun updateResponseData(id: Int, status: WidgetStatus, responseData: ByteArray?) {
        dataSource.updateResponseData(id, status, responseData)
    }

    override suspend fun get(widgetIds: List<Int>): List<SavedWidgetContentState> {
        return dataSource.getAll(false).filter { it.status != WidgetStatus.PENDING }.map { dto ->
            val jsonEntity = jsonParser.parse<WidgetSettingsJsonEntity>(dto.content)
            val location = jsonEntity.getLocation()

            if (dto.status == WidgetStatus.RESPONSE_SUCCESS) {
                val responseData = dto.responseData?.let {
                    try {
                        jsonParser.parse<WidgetResponseDBModel>(it)
                    } catch (e: Exception) {
                        null
                    }
                }
                if (responseData == null) {
                    dataSource.deleteById(dto.id)
                    SavedWidgetContentState.Failure(id = dto.id,
                        widgetType = WidgetType.fromKey(dto.widgetType),
                        updatedAt = ZonedDateTime.parse(dto.updatedAt),
                        locationType = location.locationType)
                } else {
                    SavedWidgetContentState.Success(id = dto.id,
                        widgetType = WidgetType.fromKey(dto.widgetType),
                        updatedAt = ZonedDateTime.parse(dto.updatedAt),
                        locationType = location.locationType,
                        address = responseData.address,
                        latitude = responseData.latitude,
                        longitude = responseData.longitude,
                        entities = responseData.entities.map {
                            it.toWeatherEntity(jsonParser)
                        })
                }
            } else {
                SavedWidgetContentState.Failure(id = dto.id,
                    widgetType = WidgetType.fromKey(dto.widgetType),
                    updatedAt = ZonedDateTime.parse(dto.updatedAt),
                    locationType = location.locationType)
            }
        }
    }
}