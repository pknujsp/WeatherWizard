package io.github.pknujsp.weatherwizard.feature.widget.worker

import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.domain.weather.GetCurrentWeatherUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetDailyForecastUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetHourlyForecastUseCase
import io.github.pknujsp.weatherwizard.core.model.EntityModel
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.nominatim.ReverseGeoCodeEntity
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataMajorCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetEntity
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewModel
import io.github.pknujsp.weatherwizard.feature.widget.worker.model.RequestEntity
import io.github.pknujsp.weatherwizard.feature.widget.worker.model.ResponseEntity
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.properties.Delegates

class WidgetRemoteViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getHourlyForecastUseCase: GetHourlyForecastUseCase,
    private val getDailyForecastUseCase: GetDailyForecastUseCase,
    private val appSettingsRepository: SettingsRepository,
    private val widgetRepository: WidgetRepository,
    private val nominatimRepository: NominatimRepository,
) : RemoteViewModel() {

    private val requestEntity = RequestEntity()
    private val responseEntity = ResponseEntity()

    private var widgetEntities: List<WidgetEntity> by Delegates.notNull()
    private var reverseGeoCode: Result<ReverseGeoCodeEntity>? = null

    var currentLocation: Pair<Float, Float>? = null

    fun hasCurrentLocationType(): Boolean {
        return widgetEntities.any { it.content.getLocationType() is LocationType.CurrentLocation }
    }

    fun isInitializng(appWidgetIds: IntArray): Boolean = widgetEntities.isEmpty() or !widgetEntities.any { it.id in appWidgetIds }

    suspend fun load() {
        widgetEntities = widgetRepository.getAll().first()
    }

    suspend fun updateWidgets() {
        loadWeatherData()
    }

    private suspend fun loadWeatherData() {
        widgetEntities.filter { it.content.getLocationType() is LocationType.CurrentLocation }.run {
            if (isNotEmpty()) {
                val (latitude, longitude) = currentLocation!!
                forEach { entity ->
                    requestEntity.addRequest(entity.id,
                        latitude,
                        longitude,
                        "",
                        entity.content.getWeatherProvider(),
                        entity.widgetType.categories)
                }
            }
        }
        widgetEntities.filter { it.content.getLocationType() is LocationType.CustomLocation }.forEach { entity ->
            entity.run {
                requestEntity.addRequest(
                    id,
                    content.latitude.toFloat(),
                    content.longitude.toFloat(),
                    content.addressName,
                    content.getWeatherProvider(),
                    widgetType.categories,
                )
            }
        }

        currentLocation?.let {
            reverseGeoCode = nominatimRepository.reverseGeoCode(it.first.toDouble(), it.second.toDouble())
        }

        requestEntity.requests.forEach { entry ->
            val latitude = entry.key.first.toDouble()
            val longitude = entry.key.second.toDouble()

            entry.value.providerMap.forEach { providerEntry ->
                val weatherProvider = providerEntry.key
                val provider = providerEntry.value

                provider.categories.forEach { category ->
                    category.request(latitude, longitude, weatherProvider, provider.requestId)
                }
            }
        }
    }

    suspend fun deleteWidgets(appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            widgetRepository.delete(appWidgetId)
        }
    }

    private suspend fun WeatherDataMajorCategory.request(
        latitude: Double, longitude: Double, weatherProvider: WeatherDataProvider, requestId: Long
    ) {
        val response: Result<EntityModel> = when (this) {
            WeatherDataMajorCategory.CURRENT_CONDITION -> getCurrentWeatherUseCase(latitude, longitude, weatherProvider, requestId)
            WeatherDataMajorCategory.HOURLY_FORECAST -> getHourlyForecastUseCase(latitude, longitude, weatherProvider, requestId)
            WeatherDataMajorCategory.DAILY_FORECAST -> getDailyForecastUseCase(latitude, longitude, weatherProvider, requestId)
            else -> TODO()
        }

        responseEntity.addResponse(requestId, this, response)
    }


}