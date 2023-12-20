package io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker

import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetSettingsEntity
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetSettingsEntityList
import io.github.pknujsp.weatherwizard.core.domain.location.CurrentLocationResultState
import io.github.pknujsp.weatherwizard.core.domain.location.GetCurrentLocationUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetWeatherDataUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewModel
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.worker.model.WidgetHeaderUiModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.supervisorScope
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.properties.Delegates

class WidgetRemoteViewModel @Inject constructor(
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val widgetRepository: WidgetRepository,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val nominatimRepository: NominatimRepository,
    @CoDispatcher(CoDispatcherType.MULTIPLE) private val dispatcher: CoroutineDispatcher,
    appSettingsRepository: SettingsRepository,
) : RemoteViewModel() {

    val units = appSettingsRepository.currentUnits.value
    private var widgetSettingsEntityList: WidgetSettingsEntityList by Delegates.notNull()

    suspend fun loadWidgets(): WidgetSettingsEntityList {
        widgetSettingsEntityList = widgetRepository.getAll().first()
        return widgetSettingsEntityList
    }

    suspend fun load(
        excludeWidgets: Set<WidgetSettingsEntity>, excludeLocationType: LocationType? = null
    ): List<WidgetHeaderUiModel> {
        if (excludeLocationType != null) {
            widgetSettingsEntityList = WidgetSettingsEntityList(widgetSettingsEntityList.widgetSettings.filter { entity ->
                entity.location.locationType != excludeLocationType
            })
        }
        if (excludeWidgets.isNotEmpty()) {
            widgetSettingsEntityList = WidgetSettingsEntityList(widgetSettingsEntityList.widgetSettings.filter { it !in excludeWidgets })
            deleteWidgets(excludeWidgets.map { it.id }.toIntArray())
        }

        return loadWeatherData()
    }

    private suspend fun loadWeatherData(): List<WidgetHeaderUiModel> {
        val weatherDataRequest = WeatherDataRequest(modelType = WeatherDataRequest.ModelType.BYTES)
        val responseMap = mutableMapOf<WidgetSettingsEntity, WeatherResponseState>()
        val requestMapWithRequestIdAndWidget = mutableMapOf<Long, MutableList<WidgetSettingsEntity>>()

        widgetSettingsEntityList.locationTypeGroups.getValue(LocationType.CurrentLocation).let { entities ->
            if (entities.isNotEmpty()) {
                when (val currentLocation = getCurrentLocationUseCase()) {
                    is CurrentLocationResultState.Success -> {
                        val geoCodeResult = nominatimRepository.reverseGeoCode(currentLocation.latitude, currentLocation.longitude)
                        geoCodeResult.onSuccess { geoCodeEntity ->
                            val locationModel = LocationTypeModel(
                                locationType = LocationType.CurrentLocation,
                                address = geoCodeEntity.simpleDisplayName,
                                country = geoCodeEntity.country,
                                latitude = currentLocation.latitude,
                                longitude = currentLocation.longitude,
                            )
                            entities.forEach {
                                val requestId = weatherDataRequest.addRequest(
                                    locationModel,
                                    it.widgetType.categories.toSet(),
                                    it.weatherProvider,
                                )
                                requestMapWithRequestIdAndWidget.getOrPut(requestId) { mutableListOf() }.add(it)
                            }
                        }.onFailure {
                            entities.forEach {
                                responseMap[it] = WeatherResponseState.Failure(-1, LocationTypeModel(), it.weatherProvider)
                            }
                        }

                    }

                    else -> {
                        entities.forEach {
                            responseMap[it] = WeatherResponseState.Failure(-1, LocationTypeModel(), it.weatherProvider)
                        }
                    }
                }
            }
        }

        widgetSettingsEntityList.locationTypeGroups.getValue(LocationType.CustomLocation).forEach {
            val requestId = weatherDataRequest.addRequest(
                it.location,
                it.widgetType.categories.toSet(),
                it.weatherProvider,
            )
            requestMapWithRequestIdAndWidget.getOrPut(requestId) { mutableListOf() }.add(it)
        }

        val responses = supervisorScope {
            weatherDataRequest.finalRequests.map { request ->
                async(dispatcher) { getWeatherDataUseCase(request, false) }
            }
        }

        responses.forEach {
            val response = it.await()
            for (widget in requestMapWithRequestIdAndWidget[response.requestId]!!) {
                responseMap[widget] = response
            }
        }

        return linkWidgetsWithResponses(responseMap, weatherDataRequest.requestedTime)
    }

    private fun linkWidgetsWithResponses(
        responses: Map<WidgetSettingsEntity, WeatherResponseState>, requestedTime: ZonedDateTime
    ): List<WidgetHeaderUiModel> {
        return responses.map { (widget, response) ->
            WidgetHeaderUiModel(widget.copy(location = if (widget.location.locationType is LocationType.CurrentLocation) {
                widget.location.copy(
                    latitude = response.location.latitude,
                    longitude = response.location.longitude,
                    address = response.location.address,
                    country = response.location.country,
                )
            } else {
                widget.location
            }), response, requestedTime)
        }
    }

    private suspend fun deleteWidgets(appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            widgetRepository.delete(appWidgetId)
        }
    }

}