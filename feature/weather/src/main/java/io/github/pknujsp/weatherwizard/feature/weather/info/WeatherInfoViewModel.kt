package io.github.pknujsp.weatherwizard.feature.weather.info


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.common.util.toTimeZone
import io.github.pknujsp.weatherwizard.core.data.favorite.FavoriteAreaListRepository
import io.github.pknujsp.weatherwizard.core.data.favorite.TargetLocationRepository
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.domain.location.CurrentLocationResultState
import io.github.pknujsp.weatherwizard.core.domain.location.GetCurrentLocationUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetWeatherDataUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.ProcessState
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.model.flickr.FlickrRequestParameters
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeather
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DetailDailyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.SimpleDailyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.DetailHourlyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.SimpleHourlyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeather
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity
import io.github.pknujsp.weatherwizard.core.ui.weather.item.DynamicDateTimeUiCreator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val favoriteAreaListRepository: FavoriteAreaListRepository,
    private val settingsRepository: SettingsRepository,
    private val targetLocationRepository: TargetLocationRepository,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val nominatimRepository: NominatimRepository,
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    @CoDispatcher(CoDispatcherType.IO) private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableWeatherMainUiState()
    val uiState: WeatherMainUiState by mutableStateOf(_uiState)

    fun initialize() {
        viewModelScope.launch(dispatcher) {
            _uiState.processState = ProcessState.Running
            val location = targetLocationRepository.getTargetLocation()
            val weatherProvider = settingsRepository.getWeatherDataProvider()

            if (location.locationType is LocationType.CurrentLocation) {
                when (val currentLocation = getCurrentLocationUseCase()) {
                    is CurrentLocationResultState.Success -> {
                        _uiState.isGpsEnabled = true
                        val targetLocation = nominatimRepository.reverseGeoCode(currentLocation.latitude, currentLocation.longitude)
                        targetLocation.onSuccess {
                            _uiState.args = RequestWeatherArguments(weatherProvider = weatherProvider,
                                location = LocationTypeModel(
                                    locationType = LocationType.CurrentLocation,
                                    latitude = currentLocation.latitude,
                                    longitude = currentLocation.longitude,
                                    address = it.simpleDisplayName,
                                    country = it.country,
                                ))
                            loadAllWeatherData()
                        }.onFailure {
                            _uiState.processState = ProcessState.Failed(FailedReason.SERVER_ERROR)
                        }
                    }

                    is CurrentLocationResultState.Failure -> {
                        _uiState.isGpsEnabled = false
                        _uiState.processState = ProcessState.Failed(currentLocation.reason)
                    }

                }
            } else {
                val targetLocation = favoriteAreaListRepository.getById(location.locationId)
                targetLocation.onFailure {
                    _uiState.processState = ProcessState.Failed(FailedReason.UNKNOWN)
                }.onSuccess {
                    _uiState.args = RequestWeatherArguments(weatherProvider = settingsRepository.getWeatherDataProvider(),
                        location = LocationTypeModel(
                            locationType = LocationType.CustomLocation,
                            latitude = it.latitude,
                            longitude = it.longitude,
                            address = it.areaName,
                            country = it.countryName,
                        ))
                    loadAllWeatherData()
                }

            }
        }
    }


    fun updateWeatherDataProvider(weatherProvider: WeatherProvider) {
        viewModelScope.launch {
            _uiState.processState = ProcessState.Running
            settingsRepository.setWeatherDataProvider(weatherProvider)
            _uiState.args = _uiState.args.copy(weatherProvider = weatherProvider)
        }
    }

    private fun loadAllWeatherData() {
        viewModelScope.launch(dispatcher) {
            uiState.args.run {
                val weatherDataRequest = WeatherDataRequest()
                weatherDataRequest.addRequest(location, weatherProvider.majorWeatherEntityTypes, weatherProvider)

                val entity = when (val result = getWeatherDataUseCase(weatherDataRequest.finalRequests[0], false)) {
                    is WeatherResponseState.Success -> result.entity
                    is WeatherResponseState.Failure -> {
                        _uiState.processState = ProcessState.Failed(FailedReason.SERVER_ERROR)
                        return@launch
                    }
                }
                val requestDateTime = ZonedDateTime.now()
                val dayNightCalculator = DayNightCalculator(location.latitude, location.longitude, requestDateTime.toTimeZone())

                val currentWeatherEntity = entity.toEntity<CurrentWeatherEntity>()
                val hourlyForecastEntity = entity.toEntity<HourlyForecastEntity>()
                val dailyForecastEntity = entity.toEntity<DailyForecastEntity>()

                val flckerRequestParameter = createFlickrRequestParameter(currentWeatherEntity.weatherCondition.value,
                    location.latitude,
                    location.longitude,
                    requestDateTime)
                val currentWeather = createCurrentWeatherUiModel(currentWeatherEntity, dayNightCalculator, requestDateTime.toCalendar())
                val simpleHourlyForecast = createSimpleHourlyForecastUiModel(hourlyForecastEntity, dayNightCalculator)
                val detailHourlyForecast = createDetailHourlyForecastUiModel(hourlyForecastEntity, dayNightCalculator)
                val simpleDailyForecast = createSimpleDailyForecastUiModel(dailyForecastEntity)
                val detailDailyForecast = createDetailDailyForecastUiModel(dailyForecastEntity)

                val yesterdayWeather = if (entity.weatherDataMajorCategories.contains(MajorWeatherEntityType.YESTERDAY_WEATHER)) {
                    val yesterdayWeatherEntity = entity.toEntity<YesterdayWeatherEntity>()
                    createYesterdayWeatherUiModel(yesterdayWeatherEntity)
                } else {
                    null
                }

                _uiState.weather = Weather(currentWeather,
                    simpleHourlyForecast,
                    detailHourlyForecast,
                    simpleDailyForecast,
                    detailDailyForecast,
                    yesterdayWeather)
                _uiState.updateLastUpdatedTime(requestDateTime)
                _uiState.flickrRequestParameters = flckerRequestParameter
                _uiState.processState = ProcessState.Succeed
            }
        }
    }


    private fun createCurrentWeatherUiModel(
        currentWeatherEntity: CurrentWeatherEntity, dayNightCalculator: DayNightCalculator, currentCalendar: java.util.Calendar
    ): CurrentWeather {
        return currentWeatherEntity.run {
            val unit = settingsRepository.currentUnits.value
            CurrentWeather(weatherCondition = weatherCondition,
                temperature = temperature.convertUnit(unit.temperatureUnit),
                feelsLikeTemperature = feelsLikeTemperature.convertUnit(unit.temperatureUnit),
                humidity = humidity,
                windSpeed = windSpeed.convertUnit(unit.windSpeedUnit),
                windDirection = windDirection,
                precipitationVolume = precipitationVolume.convertUnit(unit.precipitationUnit),
                dayNightCalculator = dayNightCalculator,
                currentCalendar = currentCalendar)
        }
    }

    private fun createSimpleHourlyForecastUiModel(
        hourlyForecastEntity: HourlyForecastEntity, dayNightCalculator: DayNightCalculator
    ): SimpleHourlyForecast {
        val unit = settingsRepository.currentUnits.value
        val simpleHourlyForecast = hourlyForecastEntity.items.mapIndexed { i, it ->
            SimpleHourlyForecast.Item(
                id = i,
                dateTime = it.dateTime,
                weatherCondition = it.weatherCondition,
                temperature = it.temperature.convertUnit(unit.temperatureUnit),
                feelsLikeTemperature = it.feelsLikeTemperature.convertUnit(unit.temperatureUnit),
                humidity = it.humidity,
                windSpeed = it.windSpeed.convertUnit(unit.windSpeedUnit),
                windDirection = it.windDirection,
                precipitationVolume = it.precipitationVolume.convertUnit(unit.precipitationUnit),
                precipitationProbability = it.precipitationProbability,
                dayNightCalculator = dayNightCalculator,
            )
        }

        val dateItems =
            DynamicDateTimeUiCreator(hourlyForecastEntity.items.map { it.dateTime.value }, SimpleHourlyForecast.itemWidth).invoke()

        return SimpleHourlyForecast(simpleHourlyForecast, dateItems)
    }

    private fun createDetailHourlyForecastUiModel(
        hourlyForecastEntity: HourlyForecastEntity, dayNightCalculator: DayNightCalculator
    ): DetailHourlyForecast {
        val unit = settingsRepository.currentUnits.value
        val formatter = DateTimeFormatter.ofPattern("M.d EEE")
        val detailHourlyForecast = hourlyForecastEntity.items.groupBy { ZonedDateTime.parse(it.dateTime.value).dayOfYear }.map {
            ZonedDateTime.parse(it.value.first().dateTime.value).format(formatter) to it.value.mapIndexed { i, item ->
                DetailHourlyForecast.Item(
                    id = i,
                    dateTime = item.dateTime,
                    weatherCondition = item.weatherCondition,
                    temperature = item.temperature.convertUnit(unit.temperatureUnit),
                    feelsLikeTemperature = item.feelsLikeTemperature.convertUnit(unit.temperatureUnit),
                    humidity = item.humidity,
                    windSpeed = item.windSpeed.convertUnit(unit.windSpeedUnit),
                    windDirection = item.windDirection,
                    precipitationVolume = item.precipitationVolume.convertUnit(unit.precipitationUnit),
                    precipitationProbability = item.precipitationProbability,
                    dayNightCalculator = dayNightCalculator,
                )
            }
        }
        return DetailHourlyForecast(detailHourlyForecast)
    }


    private fun createSimpleDailyForecastUiModel(
        dailyForecastEntity: DailyForecastEntity
    ) = SimpleDailyForecast(dailyForecastEntity, settingsRepository.currentUnits.value)

    private fun createDetailDailyForecastUiModel(
        dailyForecastEntity: DailyForecastEntity
    ) = DetailDailyForecast(dailyForecastEntity, settingsRepository.currentUnits.value)

    private fun createYesterdayWeatherUiModel(
        yesterdayWeatherEntity: YesterdayWeatherEntity
    ): YesterdayWeather {
        val temperatureUnit = settingsRepository.currentUnits.value.temperatureUnit
        return YesterdayWeather(temperature = yesterdayWeatherEntity.temperature.convertUnit(temperatureUnit))
    }

    private fun createFlickrRequestParameter(
        weatherCondition: WeatherConditionCategory, latitude: Double, longitude: Double, requestDateTime: ZonedDateTime
    ): FlickrRequestParameters = FlickrRequestParameters(
        weatherCondition = weatherCondition,
        latitude = latitude,
        longitude = longitude,
        refreshDateTime = requestDateTime,
    )
}

private class MutableWeatherMainUiState(
) : WeatherMainUiState {
    override var isGpsEnabled by mutableStateOf(false)
    override var processState: ProcessState by mutableStateOf(ProcessState.Idle)
    override var args: RequestWeatherArguments by Delegates.notNull()
    override var flickrRequestParameters: FlickrRequestParameters? by mutableStateOf(null)
    override var lastUpdatedTime: String = ""
        private set

    override var weather: Weather? by mutableStateOf(null)

    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("MM.dd HH:mm")
    }

    fun updateLastUpdatedTime(lastUpdatedTime: ZonedDateTime) {
        this.lastUpdatedTime = lastUpdatedTime.format(dateTimeFormatter)
    }

}