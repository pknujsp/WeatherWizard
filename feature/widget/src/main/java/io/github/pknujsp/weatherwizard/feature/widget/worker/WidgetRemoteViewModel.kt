package io.github.pknujsp.weatherwizard.feature.widget.worker

import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.domain.weather.GetWeatherDataUseCase
import io.github.pknujsp.weatherwizard.core.model.coordinate.Coordinate
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.nominatim.ReverseGeoCodeEntity
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetEntity
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewModel
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.weatherwizard.feature.widget.worker.model.WidgetHeaderUiModel
import io.github.pknujsp.weatherwizard.core.domain.weather.ResponseState
import kotlinx.coroutines.flow.first
import java.time.ZonedDateTime
import javax.inject.Inject

class WidgetRemoteViewModel @Inject constructor(
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val appSettingsRepository: SettingsRepository,
    private val widgetRepository: WidgetRepository,
    private val nominatimRepository: NominatimRepository,
) : RemoteViewModel() {

    val units = appSettingsRepository.currentUnits.value
    private var widgetEntities: List<WidgetEntity> = emptyList()
    private var reverseGeoCode: Result<ReverseGeoCodeEntity>? = null

    var currentLocation: Coordinate? = null

    fun widgetIdsByLocationType(locationType: LocationType): List<Int> {
        return widgetEntities.filter { it.content.locationType == locationType }.map { it.id }
    }

    suspend fun loadWidgets(): List<WidgetEntity> {
        widgetEntities = widgetRepository.getAll().first()
        return widgetEntities
    }

    suspend fun load(
        excludeAppWidgetIds: List<Int>, excludeLocationType: LocationType? = null
    ): List<WidgetHeaderUiModel> {
        excludeLocationType?.let { widgetEntities = widgetEntities.filter { entity -> entity.content.locationType != it } }
        if (excludeAppWidgetIds.isNotEmpty()) {
            widgetEntities = widgetEntities.filter { entity -> entity.id !in excludeAppWidgetIds }
            deleteWidgets(excludeAppWidgetIds.toIntArray())
        }

        return loadWeatherData()
    }

    private suspend fun loadWeatherData(): List<WidgetHeaderUiModel> {
        val weatherDataRequest = WeatherDataRequest()

        currentLocation?.let {
            reverseGeoCode = nominatimRepository.reverseGeoCode(it.latitude, it.longitude)
        }

        widgetEntities.filter { it.content.locationType == LocationType.CurrentLocation }.run {
            val (latitude, longitude) = currentLocation!!
            forEach { entity ->
                weatherDataRequest.addRequest(
                    Coordinate(latitude, longitude),
                    entity.widgetType.categories.toSet(),
                    entity.content.weatherProvider,
                )
            }
        }

        widgetEntities.filter { it.content.locationType is LocationType.CustomLocation }.forEach { entity ->
            entity.run {
                weatherDataRequest.addRequest(
                    Coordinate(content.latitude, content.longitude),
                    widgetType.categories.toSet(),
                    content.weatherProvider,
                )
            }
        }

        val requests = weatherDataRequest.requests
        val responses = requests.map { request ->
            getWeatherDataUseCase(request)
        }

        return linkWidgetsWithResponses(responses, weatherDataRequest.requestedTime)
    }

    private fun linkWidgetsWithResponses(responses: List<ResponseState>, requestedTime: ZonedDateTime): List<WidgetHeaderUiModel> {
        return widgetEntities.map {
            val addressName = when (it.content.locationType) {
                LocationType.CurrentLocation -> reverseGeoCode?.getOrNull()?.simpleDisplayName ?: ""
                is LocationType.CustomLocation -> it.content.addressName
            }

            WidgetHeaderUiModel(it.widgetType, it.id, addressName, responses.first { responseState ->
                responseState.coordinate == it.content.coordinate && responseState.weatherDataProvider == it.content.weatherProvider
            }, requestedTime)
        }
    }

    suspend fun deleteWidgets(appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            widgetRepository.delete(appWidgetId)
        }
    }


}