package io.github.pknujsp.weatherwizard.feature.widget.worker

import androidx.compose.ui.tooling.data.EmptyGroup.data
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.notification.NotificationRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.data.widget.WidgetRepository
import io.github.pknujsp.weatherwizard.core.domain.weather.GetCurrentWeatherUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetDailyForecastUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetHourlyForecastUseCase
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.nominatim.GeoCode
import io.github.pknujsp.weatherwizard.core.model.nominatim.ReverseGeoCodeEntity
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataMajorCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetEntity
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewModel
import io.github.pknujsp.weatherwizard.feature.widget.WidgetManager
import kotlinx.coroutines.flow.first
import java.time.ZonedDateTime
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

    private val request = Request()

    var widgetEntities: List<WidgetEntity> by Delegates.notNull()
        private set

    private var reverseGeoCode: ReverseGeoCodeEntity? = null

    var currentLocation: Pair<Float, Float>? = null


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
                    request.addRequest(latitude, longitude, entity.content.getWeatherProvider(), entity.widgetType.categories)
                }
            }
        }
        widgetEntities.filter { it.content.getLocationType() is LocationType.CustomLocation }.forEach { entity ->
            request.addRequest(entity.content.latitude.toFloat(),
                entity.content.longitude.toFloat(),
                entity.content.getWeatherProvider(),
                entity.widgetType.categories,
                entity.content.addressName)
            entity.widgetType
        }

        currentLocation?.let {
            reverseGeoCode = nominatimRepository.reverseGeoCode(it.first.toDouble(), it.second.toDouble()).getOrNull()
        }

        var requestId = 0L
        request.requests.forEach { entry ->
            val latitude = entry.value.latitude.toDouble()
            val longitude = entry.value.longitude.toDouble()

            entry.value.weatherProviders.forEach { weatherDataProvider ->
                entry.value.categories.forEach { category ->
                    category.request(latitude, longitude, weatherDataProvider, requestId)
                }
                requestId++
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
        when (this) {
            WeatherDataMajorCategory.CURRENT_CONDITION -> {
                getCurrentWeatherUseCase(latitude, longitude, weatherProvider, requestId)
            }

            WeatherDataMajorCategory.HOURLY_FORECAST -> {
                getHourlyForecastUseCase(latitude, longitude, weatherProvider, requestId)
            }

            WeatherDataMajorCategory.DAILY_FORECAST -> {
                getDailyForecastUseCase(latitude, longitude, weatherProvider, requestId)
            }

            else -> {}
        }
    }

    private class Request(
        val now: ZonedDateTime = ZonedDateTime.now(),
    ) {
        private val _requests: MutableMap<Pair<Float, Float>, Item> = mutableMapOf()
        val requests: Map<Pair<Float, Float>, Item> = _requests

        fun addRequest(
            latitude: Float,
            longitude: Float,
            weatherProvider: WeatherDataProvider,
            categories: Array<WeatherDataMajorCategory>,
            address: String = "",
        ) {
            _requests.getOrPut(latitude to longitude) {
                Item(latitude = latitude, longitude = longitude, address = address)
            }.run {
                if (weatherProvider !in weatherProviders) {
                    weatherProviders = weatherProviders.plusElement(weatherProvider)
                }
                categories.filter { it !in this.categories }.forEach { category ->
                    this.categories = this.categories.plusElement(category)
                }
            }
        }

        data class Item(
            var address: String,
            val latitude: Float,
            val longitude: Float,
            var weatherProviders: Set<WeatherDataProvider> = emptySet(),
            var categories: Set<WeatherDataMajorCategory> = emptySet(),
        )
    }
}