package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetSettingsEntity
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetSettingsEntityList
import io.github.pknujsp.weatherwizard.core.domain.location.CurrentLocationState
import io.github.pknujsp.weatherwizard.core.domain.location.GetCurrentLocationUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetWeatherDataUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewModel
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.worker.model.WidgetRemoteViewUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import kotlin.properties.Delegates

class WidgetRemoteViewModel @Inject constructor(
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val widgetRepository: WidgetRepository,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val nominatimRepository: NominatimRepository,
) : RemoteViewModel() {

    private var widgetSettingsEntityList: WidgetSettingsEntityList by Delegates.notNull()

    suspend fun loadWidgets(widgetId: Int, requestedAction: Int): WidgetSettingsEntityList {
        widgetSettingsEntityList = widgetRepository.getAll()

        val filteredList = when (requestedAction) {
            LoadWidgetDataArgument.NEW_WIDGET -> widgetSettingsEntityList.widgetSettings.filter { it.id == widgetId }
            LoadWidgetDataArgument.UPDATE_ONLY_FAILED -> widgetSettingsEntityList.widgetSettings.filter { it.status == WidgetStatus.RESPONSE_FAILURE }
            else -> widgetSettingsEntityList.widgetSettings
        }

        widgetSettingsEntityList = WidgetSettingsEntityList(filteredList)
        return widgetSettingsEntityList
    }

    suspend fun load(
        excludeWidgets: List<Int>
    ): List<WidgetRemoteViewUiState> {
        if (excludeWidgets.isNotEmpty()) {
            widgetSettingsEntityList = WidgetSettingsEntityList(widgetSettingsEntityList.widgetSettings.filter { it.id !in excludeWidgets })
        }

        return loadWeatherData()
    }

    private suspend fun loadWeatherData(): List<WidgetRemoteViewUiState> {
        val weatherDataRequest = WeatherDataRequest()
        val responseMap = mutableMapOf<WidgetSettingsEntity, WeatherResponseState>()
        val requestMapWithRequestIdAndWidget = mutableMapOf<Long, MutableList<WidgetSettingsEntity>>()
        val locationMap = mutableMapOf<WeatherDataRequest.Coordinate, String>()

        widgetSettingsEntityList.locationTypeGroups[LocationType.CurrentLocation]?.let { entities ->
            if (entities.isNotEmpty()) {
                when (val currentLocation = getCurrentLocationUseCase()) {
                    is CurrentLocationState.Success -> {
                        val geoCodeResult = nominatimRepository.reverseGeoCode(currentLocation.latitude, currentLocation.longitude)
                        geoCodeResult.onSuccess { geoCodeEntity ->
                            val coordinate = WeatherDataRequest.Coordinate(
                                latitude = currentLocation.latitude,
                                longitude = currentLocation.longitude,
                            )
                            locationMap[coordinate] = geoCodeEntity.simpleDisplayName
                            entities.forEach {
                                val requestId = weatherDataRequest.addRequest(
                                    coordinate,
                                    it.widgetType.categories.toSet(),
                                    it.weatherProvider,
                                )
                                requestMapWithRequestIdAndWidget.getOrPut(requestId) { mutableListOf() }.add(it)
                            }
                        }.onFailure {
                            entities.forEach {
                                responseMap[it] = WeatherResponseState.Failure(-1, WeatherDataRequest.Coordinate(), it.weatherProvider)
                            }
                        }

                    }

                    else -> {
                        entities.forEach {
                            responseMap[it] = WeatherResponseState.Failure(-1, WeatherDataRequest.Coordinate(), it.weatherProvider)
                        }
                    }
                }
            }
        }

        widgetSettingsEntityList.locationTypeGroups[LocationType.CustomLocation]?.forEach {
            val coordinate = WeatherDataRequest.Coordinate(
                latitude = it.location.latitude,
                longitude = it.location.longitude,
            )
            locationMap[coordinate] = it.location.address
            val requestId = weatherDataRequest.addRequest(
                coordinate,
                it.widgetType.categories.toSet(),
                it.weatherProvider,
            )
            requestMapWithRequestIdAndWidget.getOrPut(requestId) { mutableListOf() }.add(it)
        }

        val responses = coroutineScope {
            weatherDataRequest.finalRequests.map { request ->
                async { getWeatherDataUseCase(request, false) }
            }
        }

        for (response in responses) {
            response.await().run {
                for (widget in requestMapWithRequestIdAndWidget.getValue(requestId)) {
                    responseMap[widget] = this
                }
            }
        }

        return linkWidgetsWithResponses(responseMap, locationMap)
    }

    private fun linkWidgetsWithResponses(
        responses: Map<WidgetSettingsEntity, WeatherResponseState>, locationMap: Map<WeatherDataRequest.Coordinate, String>
    ): List<WidgetRemoteViewUiState> {
        return responses.map { (widget, response) ->
            WidgetRemoteViewUiState(
                widget = widget,
                lastUpdated = if (response is WeatherResponseState.Success) response.entity.responseTime else null,
                address = locationMap[response.location],
                isSuccessful = response is WeatherResponseState.Success,
                model = if (response is WeatherResponseState.Success) response.entity else null,
                latitude = response.location.latitude,
                longitude = response.location.longitude,
            )
        }
    }

    suspend fun updateResponseData(id: Int, status: WidgetStatus, responseData: ByteArray? = null) {
        widgetRepository.updateResponseData(id, status, responseData)
    }
}