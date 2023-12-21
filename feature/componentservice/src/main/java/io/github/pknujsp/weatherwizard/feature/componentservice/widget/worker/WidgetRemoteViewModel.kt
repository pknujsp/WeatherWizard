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
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetStatus
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewModel
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.worker.model.WidgetRemoteViewUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
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

    suspend fun loadWidgets(widgetIds: Array<Int>): WidgetSettingsEntityList {
        widgetSettingsEntityList = widgetRepository.getAll().first()
        if (widgetIds.isNotEmpty()) {
            widgetSettingsEntityList = WidgetSettingsEntityList(widgetSettingsEntityList.widgetSettings.filter { it.id in widgetIds })
        }
        return widgetSettingsEntityList
    }

    suspend fun load(
        excludeWidgets: Set<WidgetSettingsEntity>, excludeLocationType: LocationType? = null
    ): List<WidgetRemoteViewUiState> {
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

    private suspend fun loadWeatherData(): List<WidgetRemoteViewUiState> {
        val weatherDataRequest = WeatherDataRequest()
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

        val responses = coroutineScope {
            weatherDataRequest.finalRequests.map { request ->
                async(dispatcher) { getWeatherDataUseCase(request, false) }
            }
        }

        for (response in responses) {
            response.await().run {
                for (widget in requestMapWithRequestIdAndWidget.getValue(requestId)) {
                    responseMap[widget] = this
                }
            }
        }

        return linkWidgetsWithResponses(responseMap)
    }

    private fun linkWidgetsWithResponses(
        responses: Map<WidgetSettingsEntity, WeatherResponseState>
    ): List<WidgetRemoteViewUiState> {
        return responses.map { (widget, response) ->
            WidgetRemoteViewUiState(
                widget = widget,
                lastUpdated = if (response is WeatherResponseState.Success) response.entity.responseTime else null,
                address = response.location.address,
                isSuccessful = response is WeatherResponseState.Success,
                model = if (response is WeatherResponseState.Success) response.entity else null,
                latitude = response.location.latitude,
                longitude = response.location.longitude,
            )
        }
    }

    private suspend fun deleteWidgets(appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            widgetRepository.delete(appWidgetId)
        }
    }

    suspend fun updateResponseData(id: Int, status: WidgetStatus, responseData: ByteArray = byteArrayOf()) {
        widgetRepository.updateResponseData(id, status, responseData)
    }
}