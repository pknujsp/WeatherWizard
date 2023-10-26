package io.github.pknujsp.weatherwizard.feature.widget.worker

import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.domain.weather.GetCurrentWeatherUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetDailyForecastUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetHourlyForecastUseCase
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.coordinate.Coordinate
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.nominatim.ReverseGeoCodeEntity
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataMajorCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetEntity
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewModel
import io.github.pknujsp.weatherwizard.feature.widget.worker.model.EntityMapper
import io.github.pknujsp.weatherwizard.feature.widget.worker.model.RequestEntity
import io.github.pknujsp.weatherwizard.feature.widget.worker.model.WidgetUiModel
import io.github.pknujsp.weatherwizard.feature.widget.worker.model.WidgetUiState
import kotlinx.coroutines.flow.first
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class WidgetRemoteViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getHourlyForecastUseCase: GetHourlyForecastUseCase,
    private val getDailyForecastUseCase: GetDailyForecastUseCase,
    private val appSettingsRepository: SettingsRepository,
    private val widgetRepository: WidgetRepository,
    private val nominatimRepository: NominatimRepository,
) : RemoteViewModel() {

    private val requestEntity = RequestEntity()

    var widgetEntities: List<WidgetEntity> = emptyList()
        private set
    private var reverseGeoCode: Result<ReverseGeoCodeEntity>? = null

    var currentLocation: Pair<Double, Double>? = null

    inline fun <reified T : LocationType> widgetIdsByLocationType(): IntArray {
        return widgetEntities.filter { it.content.getLocationType() is T }.map { it.id }.toIntArray()
    }

    fun isInitializng(appWidgetIds: IntArray): Boolean = widgetEntities.isEmpty() or !widgetEntities.any { it.id in appWidgetIds }

    suspend fun init() {
        widgetEntities = widgetRepository.getAll().first()
    }

    suspend fun load(excludeAppWidgetIds: IntArray? = null) {
        excludeAppWidgetIds?.let {
            widgetEntities = widgetEntities.filter { entity -> entity.id !in it }
        }
        loadWeatherData()
    }

    private suspend fun loadWeatherData(): List<WidgetUiModel<WidgetUiState>> {
        currentLocation?.let {
            reverseGeoCode = nominatimRepository.reverseGeoCode(it.first, it.second)
        }

        widgetEntities.filter { it.content.getLocationType() is LocationType.CurrentLocation }.run {
            if (isNotEmpty()) {
                val (latitude, longitude) = currentLocation!!
                val address = reverseGeoCode!!.getOrThrow().simpleDisplayName

                forEach { entity ->
                    requestEntity.addRequest(entity.id,
                        entity.widgetType,
                        Coordinate(latitude, longitude),
                        entity.content.getWeatherProvider(),
                        address)
                }
            }
        }
        widgetEntities.filter { it.content.getLocationType() is LocationType.CustomLocation }.forEach { entity ->
            entity.run {
                requestEntity.addRequest(
                    id,
                    widgetType,
                    Coordinate(content.latitude, content.longitude),
                    content.getWeatherProvider(),
                    content.addressName,
                )
            }
        }
        requestEntity.requests.values.forEach { request ->
            request.headerMap.forEach { (weatherProvider, header) ->
                header.categories.forEach { category ->
                    category.request(request.coordinate, header, weatherProvider)
                }
            }
        }

        val widgetUiStateMap = requestEntity.requests.values.flatMap { request ->
            request.toResponseEntity()
        }.run {
            EntityMapper(this, appSettingsRepository.currentUnits.value, requestEntity.now).invoke()
        }

        val updatedTime = DateTimeFormatter.ofPattern("M.d EEE HH:mm").format(requestEntity.now)
        return widgetUiStateMap.flatMap { (info, widgetUiState) ->
            val request = requestEntity.requests[info.second]!!
            val requestHeader = request.headerMap[info.third]!!
            val address = request.address

            requestHeader.appWidgetIds.map { (_, appWidgetId) ->
                WidgetUiModel(appWidgetId, address, updatedTime, widgetUiState)
            }
        }
    }

    suspend fun deleteWidgets(appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            widgetRepository.delete(appWidgetId)
        }
    }

    private suspend fun WeatherDataMajorCategory.request(
        coordinate: Coordinate,
        header: RequestEntity.Request.Header,
        weatherProvider: WeatherDataProvider,
    ) {
        val response: Result<EntityModel> = when (this) {
            WeatherDataMajorCategory.CURRENT_CONDITION -> getCurrentWeatherUseCase(coordinate.latitude,
                coordinate.longitude,
                weatherProvider,
                header.requestId)

            WeatherDataMajorCategory.HOURLY_FORECAST -> getHourlyForecastUseCase(coordinate.latitude,
                coordinate.longitude,
                weatherProvider,
                header.requestId)

            WeatherDataMajorCategory.DAILY_FORECAST -> getDailyForecastUseCase(coordinate.latitude,
                coordinate.longitude,
                weatherProvider,
                header.requestId)

            else -> {
                throw IllegalArgumentException("Unknown category: $this")
            }
        }

        header.addResponse(response)
    }


}