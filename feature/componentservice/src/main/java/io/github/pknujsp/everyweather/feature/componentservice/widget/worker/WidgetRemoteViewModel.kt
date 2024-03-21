package io.github.pknujsp.everyweather.feature.componentservice.widget.worker

import io.github.pknujsp.everyweather.core.data.widget.WidgetRepository
import io.github.pknujsp.everyweather.core.data.widget.WidgetSettingsEntity
import io.github.pknujsp.everyweather.core.data.widget.WidgetSettingsEntityList
import io.github.pknujsp.everyweather.core.domain.location.GetCurrentLocationAddress
import io.github.pknujsp.everyweather.core.domain.weather.GetWeatherDataUseCase
import io.github.pknujsp.everyweather.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.everyweather.core.domain.weather.WeatherResponseState
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.model.widget.WidgetStatus
import io.github.pknujsp.everyweather.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.everyweather.core.widgetnotification.remoteview.RemoteViewModel
import io.github.pknujsp.everyweather.core.widgetnotification.widget.worker.model.WidgetRemoteViewUiState
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.properties.Delegates

class WidgetRemoteViewModel
    @Inject
    constructor(
        private val getWeatherDataUseCase: GetWeatherDataUseCase,
        private val widgetRepository: WidgetRepository,
        getCurrentLocationUseCase: GetCurrentLocationAddress,
    ) : RemoteViewModel(getCurrentLocationUseCase) {
        private var widgets: WidgetSettingsEntityList by Delegates.notNull()

        suspend fun loadWidgets(
            widgetId: Int,
            requestedAction: Int,
        ): WidgetSettingsEntityList {
            widgets = widgetRepository.getAll()

            val filteredList =
                when (requestedAction) {
                    LoadWidgetDataArgument.NEW_WIDGET -> widgets.widgetSettings.filter { it.id == widgetId }
                    LoadWidgetDataArgument.UPDATE_ONLY_FAILED -> widgets.widgetSettings.filter { it.status == WidgetStatus.RESPONSE_FAILURE }
                    else -> widgets.widgetSettings
                }

            widgets = WidgetSettingsEntityList(filteredList)
            return widgets
        }

        suspend fun load(excludeWidgets: List<Int>): List<WidgetRemoteViewUiState> {
            if (excludeWidgets.isNotEmpty()) {
                widgets = WidgetSettingsEntityList(widgets.widgetSettings.filter { it.id !in excludeWidgets })
            }

            return loadWeatherData()
        }

        private suspend fun loadWeatherData(): List<WidgetRemoteViewUiState> {
            val requestBuilder = WeatherDataRequest.Builder()
            val requestMap = addRequests(requestBuilder)
            val requests = requestBuilder.build()

            val responses =
                coroutineScope {
                    requests.map { request ->
                        async { getWeatherDataUseCase(request, false) }
                    }
                }.let { defferedList ->
                    defferedList.map { it.await() }
                }

            return createUiStates(requestMap, responses)
        }

        private fun createUiStates(
            requestMap: Map<WidgetSettingsEntity, WeatherDataRequest.Coordinate>,
            responses: List<WeatherResponseState>,
        ): List<WidgetRemoteViewUiState> {
            val responseMap =
                responses.groupBy {
                    it.location
                }
            val uiStates = mutableListOf<WidgetRemoteViewUiState>()

            for (widget in widgets.widgetSettings) {
                if (widget !in requestMap.keys) {
                    uiStates.add(WidgetRemoteViewUiState(widget = widget, isSuccessful = false))
                    continue
                }

                val coordinate = requestMap[widget]!!
                val weatherProviders = widget.weatherProviders

                val responsesByWeatherProvider =
                    responseMap[coordinate]!!.filter {
                        it.weatherProvider in weatherProviders
                    }

                val entities =
                    responsesByWeatherProvider.filterIsInstance<WeatherResponseState.Success>().map { response ->
                        WidgetRemoteViewUiState.EntityWithWeatherProvider(
                            weatherProvider = response.weatherProvider,
                            entity = response.entity,
                        )
                    }

                if (entities.isNotEmpty()) {
                    uiStates.add(
                        WidgetRemoteViewUiState(
                            widget = widget,
                            isSuccessful = true,
                            model = entities,
                            address = coordinate.address,
                            latitude = coordinate.latitude,
                            longitude = coordinate.longitude,
                            lastUpdated = entities[0].entity.responseTime,
                        ),
                    )
                } else {
                    uiStates.add(WidgetRemoteViewUiState(widget = widget, isSuccessful = false))
                }
            }

            return uiStates
        }

        private suspend fun addRequests(
            requestBuilder: WeatherDataRequest.Builder,
        ): Map<WidgetSettingsEntity, WeatherDataRequest.Coordinate> {
            val requestWidgetMap = mutableMapOf<WidgetSettingsEntity, WeatherDataRequest.Coordinate>()

            if (LocationType.CurrentLocation in widgets.locationTypeGroups && widgets.locationTypeGroups[LocationType.CurrentLocation]!!.isNotEmpty()) {
                val widgetEntities = widgets.locationTypeGroups[LocationType.CurrentLocation]!!
                when (val currentLocation = getCurrentLocation().first()) {
                    is CurrentLocationResult.Success -> {
                        val coordinate =
                            WeatherDataRequest.Coordinate(
                                latitude = currentLocation.latitude,
                                longitude = currentLocation.longitude,
                                address = currentLocation.address,
                            )
                        widgetEntities.forEach {
                            requestWidgetMap[it] = coordinate
                            it.weatherProviders.forEach { weatherProvider ->
                                requestBuilder.add(
                                    coordinate,
                                    it.widgetType.categories,
                                    weatherProvider,
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }

            widgets.locationTypeGroups[LocationType.CustomLocation]?.forEach {
                val coordinate =
                    WeatherDataRequest.Coordinate(
                        latitude = it.location.latitude,
                        longitude = it.location.longitude,
                        address = it.location.address,
                    )

                it.weatherProviders.forEach { weatherProvider ->
                    requestWidgetMap[it] = coordinate
                    requestBuilder.add(
                        coordinate,
                        it.widgetType.categories,
                        weatherProvider,
                    )
                }
            }

            return requestWidgetMap
        }

        suspend fun updateResponseData(
            id: Int,
            status: WidgetStatus,
            responseData: ByteArray? = null,
        ) {
            widgetRepository.updateResponseData(id, status, responseData)
        }
    }
