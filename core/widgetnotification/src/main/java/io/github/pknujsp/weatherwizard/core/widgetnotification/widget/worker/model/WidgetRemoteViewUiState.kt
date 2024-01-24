package io.github.pknujsp.weatherwizard.core.widgetnotification.widget.worker.model

import io.github.pknujsp.weatherwizard.core.data.widget.WidgetResponseDBModel
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetSettingsEntity
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseEntity
import io.github.pknujsp.weatherwizard.core.data.mapper.JsonParser
import io.github.pknujsp.weatherwizard.core.model.airquality.AirQualityEntity
import io.github.pknujsp.weatherwizard.core.model.remoteviews.RemoteViewUiState
import io.github.pknujsp.weatherwizard.core.model.weather.base.WeatherEntityModel
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity
import java.time.ZonedDateTime

class WidgetRemoteViewUiState(
    val widget: WidgetSettingsEntity,
    override val isSuccessful: Boolean,
    override val lastUpdated: ZonedDateTime? = null,
    override val address: String? = null,
    override val model: List<EntityWithWeatherProvider>? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
) : RemoteViewUiState<List<WidgetRemoteViewUiState.EntityWithWeatherProvider>> {

    class EntityWithWeatherProvider(
        val weatherProvider: WeatherProvider,
        val entity: WeatherResponseEntity,
    )

    private companion object {
        fun realTypeParseToByteArray(jsonParser: JsonParser, majorWeatherEntityType: MajorWeatherEntityType, model: WeatherEntityModel) =
            when (majorWeatherEntityType) {
                MajorWeatherEntityType.CURRENT_CONDITION -> jsonParser.parseToByteArray<CurrentWeatherEntity>(model as CurrentWeatherEntity)
                MajorWeatherEntityType.YESTERDAY_WEATHER -> jsonParser.parseToByteArray<YesterdayWeatherEntity>(model as YesterdayWeatherEntity)
                MajorWeatherEntityType.HOURLY_FORECAST -> jsonParser.parseToByteArray<HourlyForecastEntity>(model as HourlyForecastEntity)
                MajorWeatherEntityType.DAILY_FORECAST -> jsonParser.parseToByteArray<DailyForecastEntity>(model as DailyForecastEntity)
                MajorWeatherEntityType.AIR_QUALITY -> jsonParser.parseToByteArray<AirQualityEntity>(model as AirQualityEntity)
            }
    }

    fun toWidgetResponseDBModel(jsonParser: JsonParser) = WidgetResponseDBModel(entities = model!!.map { entity ->
        WidgetResponseDBModel.EntityWithWeatherProvider(weatherProvider = entity.weatherProvider.key,
            entities = entity.entity.export(widget.widgetType.categories.toSet()).map {
                WidgetResponseDBModel.Entity(it.key.name, realTypeParseToByteArray(jsonParser, it.key, it.value))
            })
    }, address = address!!, latitude = latitude!!, longitude = longitude!!)
}