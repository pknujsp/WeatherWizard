package io.github.pknujsp.weatherwizard.feature.widget.worker

import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetSettingsEntity
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetSettingsEntityList
import io.github.pknujsp.weatherwizard.core.domain.location.GetCurrentLocationUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetWeatherDataUseCase
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.domain.location.CurrentLocationResultState
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewModel
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.weatherwizard.feature.widget.worker.model.WidgetHeaderUiModel
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationModel
import kotlinx.coroutines.flow.first
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.properties.Delegates

class WidgetRemoteViewModel @Inject constructor(
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val appSettingsRepository: SettingsRepository,
    private val widgetRepository: WidgetRepository,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
) : RemoteViewModel() {

    val units = appSettingsRepository.currentUnits.value
    private var widgetSettingsEntityList: WidgetSettingsEntityList by Delegates.notNull()

    suspend fun loadWidgets(): WidgetSettingsEntityList {
        widgetSettingsEntityList = widgetRepository.getAll().first()
        return widgetSettingsEntityList
    }

    suspend fun load(
        excludeAppWidgetIds: List<Int>, excludeLocationType: LocationType? = null
    ): List<WidgetHeaderUiModel> {
        if (excludeLocationType != null) {
            widgetSettingsEntityList = WidgetSettingsEntityList(widgetSettingsEntityList.widgetSettings.filter { entity ->
                entity.locationType != excludeLocationType
            })
        }
        if (excludeAppWidgetIds.isNotEmpty()) {
            widgetSettingsEntityList =
                WidgetSettingsEntityList(widgetSettingsEntityList.widgetSettings.filter { entity -> entity.id !in excludeAppWidgetIds })
            deleteWidgets(excludeAppWidgetIds.toIntArray())
        }

        return loadWeatherData()
    }

    private suspend fun loadWeatherData(): List<WidgetHeaderUiModel> {
        val weatherDataRequest = WeatherDataRequest()
        val responseMap = mutableMapOf<WidgetSettingsEntity, WeatherResponseState>()
        val requestMapWithRequestIdAndWidget = mutableMapOf<Long, MutableList<WidgetSettingsEntity>>()

        widgetSettingsEntityList.locationTypeGroups.getValue(LocationType.CurrentLocation()).let { entities ->
            if (entities.isNotEmpty()) {
                when (val currentLocation = getCurrentLocationUseCase()) {
                    is CurrentLocationResultState.Success -> {
                        val location = currentLocation.location
                        entities.forEach {
                            val requestId = weatherDataRequest.addRequest(
                                location,
                                it.widgetType.categories.toSet(),
                                it.weatherProvider,
                            )
                            requestMapWithRequestIdAndWidget.getOrPut(requestId) { mutableListOf() }.add(it)
                        }
                    }

                    else -> {
                        val emptyLocation = LocationModel(0.0, 0.0, "")
                        entities.forEach {
                            responseMap[it] = WeatherResponseState.Failure(-1, emptyLocation, it.weatherProvider)
                        }
                    }
                }
            }
        }

        widgetSettingsEntityList.locationTypeGroups.getValue(LocationType.CustomLocation()).forEach {
            val requestId = weatherDataRequest.addRequest(
                (it.locationType as LocationType.CustomLocation).run {
                    LocationModel(
                        latitude,
                        longitude,
                        address,
                    )
                },
                it.widgetType.categories.toSet(),
                it.weatherProvider,
            )
            requestMapWithRequestIdAndWidget.getOrPut(requestId) { mutableListOf() }.add(it)
        }

        weatherDataRequest.requests.forEach { request ->
            val response = getWeatherDataUseCase(request)
            for (widget in requestMapWithRequestIdAndWidget[request.requestId]!!) {
                responseMap[widget] = response
            }
        }

        return linkWidgetsWithResponses(responseMap, weatherDataRequest.requestedTime)
    }

    private fun linkWidgetsWithResponses(
        responses: Map<WidgetSettingsEntity, WeatherResponseState>, requestedTime: ZonedDateTime
    ): List<WidgetHeaderUiModel> {
        return responses.map { (widget, response) ->
            WidgetHeaderUiModel(widget.copy(
                locationType = if (widget.locationType is LocationType.CurrentLocation) LocationType.CurrentLocation(
                    latitude = response.coordinate.latitude,
                    longitude = response.coordinate.longitude,
                    address = response.coordinate.address,
                ) else widget.locationType,
            ), response, requestedTime)
        }
    }

    suspend fun deleteWidgets(appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            widgetRepository.delete(appWidgetId)
        }
    }


}