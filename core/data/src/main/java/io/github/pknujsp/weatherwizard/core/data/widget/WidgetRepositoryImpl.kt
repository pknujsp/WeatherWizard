package io.github.pknujsp.weatherwizard.core.data.widget

import io.github.pknujsp.weatherwizard.core.data.mapper.JsonParser
import io.github.pknujsp.weatherwizard.core.database.widget.WidgetDto
import io.github.pknujsp.weatherwizard.core.database.widget.WidgetLocalDataSource
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import java.time.ZonedDateTime

class WidgetRepositoryImpl(
    private val dataSource: WidgetLocalDataSource, private val jsonParser: JsonParser
) : WidgetRepository {


    override suspend fun getAll(): WidgetSettingsEntityList = dataSource.getAll(true).let {
        val list = it.map { dto ->
            val jsonEntity = jsonParser.parse<WidgetSettingsJsonEntity>(dto.content)
            WidgetSettingsEntity(id = dto.id,
                location = jsonEntity.getLocation(),
                weatherProviders = jsonEntity.getWeatherProviders(),
                status = dto.status,
                widgetType = WidgetType.fromKey(dto.widgetType))
        }

        WidgetSettingsEntityList(list)
    }

    override suspend fun add(entity: WidgetSettingsEntity): Int {
        val jsonEntity = WidgetSettingsJsonEntity(
            weatherProviders = entity.weatherProviders.map { it.key },
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
            weatherProviders = jsonEntity.getWeatherProviders(),
            widgetType = WidgetType.fromKey(dto.widgetType))
    }

    override suspend fun delete(id: Int) {
        dataSource.deleteById(id)
    }

    override suspend fun deleteAll() {
        dataSource.deleteAll()
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
                            SavedWidgetContentState.Success.EntityWithWeatherProvider(weatherProvider = WeatherProvider.fromKey(it.weatherProvider),
                                entities = it.entities.map { entity ->
                                    entity.toWeatherEntity(jsonParser)
                                })
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