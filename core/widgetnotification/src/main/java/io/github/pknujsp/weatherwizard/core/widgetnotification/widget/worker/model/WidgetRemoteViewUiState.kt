package io.github.pknujsp.weatherwizard.core.widgetnotification.widget.worker.model

import io.github.pknujsp.weatherwizard.core.data.widget.WidgetSettingsEntity
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseEntity
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.JsonParser
import io.github.pknujsp.weatherwizard.core.model.UiModel
import io.github.pknujsp.weatherwizard.core.model.remoteviews.RemoteViewUiState
import java.time.ZonedDateTime

class WidgetRemoteViewUiState(
    val widget: WidgetSettingsEntity,
    override val lastUpdated: ZonedDateTime?,
    override val address: String?,
    override val isSuccessful: Boolean,
    override val model: WeatherResponseEntity?,
) : RemoteViewUiState<WeatherResponseEntity> {
    fun toWidgetResponseDBModel(jsonParser: JsonParser) =
        WidgetResponseDBModel(address = address!!, entities = model!!.export(widget.widgetType.categories.toSet()).map {
            WidgetResponseDBModel.Entity(it.key.name, jsonParser.parseToByteArray(it.value))
        })
}