package io.github.pknujsp.everyweather.feature.weather.info

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.everyweather.core.common.FailedReason
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.common.util.DayNightCalculator
import io.github.pknujsp.everyweather.core.common.util.toCalendar
import io.github.pknujsp.everyweather.core.common.util.toTimeZone
import io.github.pknujsp.everyweather.core.data.favorite.FavoriteAreaListRepository
import io.github.pknujsp.everyweather.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.everyweather.core.data.settings.SettingsRepository
import io.github.pknujsp.everyweather.core.domain.location.CurrentLocationState
import io.github.pknujsp.everyweather.core.domain.location.GetCurrentLocationCoordinate
import io.github.pknujsp.everyweather.core.domain.weather.GetWeatherDataUseCase
import io.github.pknujsp.everyweather.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.everyweather.core.domain.weather.WeatherResponseState
import io.github.pknujsp.everyweather.core.model.coordinate.LocationType
import io.github.pknujsp.everyweather.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.everyweather.core.model.weather.TargetLocationModel
import io.github.pknujsp.everyweather.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.everyweather.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.everyweather.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.everyweather.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.everyweather.core.model.weather.yesterday.YesterdayWeatherEntity
import io.github.pknujsp.everyweather.feature.weather.info.currentweather.model.CurrentWeather
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.DailyForecastModelMapper
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model.DetailDailyForecast
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.DetailHourlyForecastModelMapper
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.HourlyForecastModelMapper
import io.github.pknujsp.everyweather.feature.weather.summary.WeatherSummaryPrompt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class WeatherContentViewModel
@Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val getCurrentLocationUseCase: GetCurrentLocationCoordinate,
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val favoriteAreaListRepository: FavoriteAreaListRepository,
    @CoDispatcher(CoDispatcherType.IO) private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val units get() = settingsRepository.settings.replayCache.last().units
    private var loadWeatherDataJob: Job? = null
    private var targetLocationJob: Job? = null

    private var baseLocation: SelectedLocationModel? = null

    var isLoading: Boolean by mutableStateOf(false)
        private set

    private val mutableTargetLocations = MutableSharedFlow<TargetLocationModel>(1, 0, BufferOverflow.DROP_OLDEST)

    private val targetLocation = mutableTargetLocations.combine(settingsRepository.settings) { location, settings ->
        location to settings.weatherProvider
    }.onEach { (location, weatherProvider) ->
        isLoading = true
        loadAllWeatherData(RequestWeatherArguments(weatherProvider, location))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val mutableUiState = MutableSharedFlow<WeatherContentUiState>(1, 0, BufferOverflow.DROP_OLDEST)

    val uiState = mutableUiState.onEach {
        isLoading = false
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun load(selectedLocationModel: SelectedLocationModel) {
        viewModelScope.launch {
            baseLocation = selectedLocationModel

            targetLocationJob?.cancel()
            loadWeatherDataJob?.cancel()
            isLoading = true

            targetLocationJob = viewModelScope.launch {
                val result = withContext(dispatcher) {
                    if (selectedLocationModel.locationType is LocationType.CurrentLocation) {
                        loadCurrentLocation().first()
                    } else {
                        val favoriteLocation = favoriteAreaListRepository.getById(selectedLocationModel.locationId).getOrThrow()
                        TargetLocationModel(
                            latitude = favoriteLocation.latitude,
                            longitude = favoriteLocation.longitude,
                            locationType = LocationType.CustomLocation,
                            customLocationId = selectedLocationModel.locationId,
                        ) to null
                    }
                }

                result.first?.run {
                    mutableTargetLocations.emit(this)
                } ?: run {
                    mutableUiState.emit(WeatherContentUiState.Error(result.second!!))
                }
            }
        }
    }

    fun isLoadedLocation(location: SelectedLocationModel): Boolean {
        return baseLocation == location
    }


    private suspend fun loadCurrentLocation() = callbackFlow {
        val now = LocalDateTime.now()
        getCurrentLocationUseCase(true)
        getCurrentLocationUseCase.currentLocationFlow.filterNotNull().filter {
            it.time >= now && it !is CurrentLocationState.Loading
        }.collect {
            when (it) {
                is CurrentLocationState.Success -> {
                    send(
                        TargetLocationModel(
                            latitude = it.latitude,
                            longitude = it.longitude,
                            locationType = LocationType.CurrentLocation,
                        ) to null,
                    )
                }

                is CurrentLocationState.Failure -> {
                    send(null to it.reason)
                }

                else -> {}
            }
            this.cancel()
        }
    }

    fun cancel() {
        viewModelScope.launch {
            targetLocationJob?.cancel()
            loadWeatherDataJob?.cancel()

            mutableUiState.emit(WeatherContentUiState.Error(FailedReason.CANCELED))
        }
    }

    private suspend fun loadAllWeatherData(args: RequestWeatherArguments) {
        loadWeatherDataJob?.cancel()
        loadWeatherDataJob = viewModelScope.launch {
            val newState = withContext(dispatcher) {
                val weatherProvider = args.weatherProvider
                val coordinate = WeatherDataRequest.Coordinate(args.targetLocation.latitude, args.targetLocation.longitude)

                val weatherDataRequestBuilder = WeatherDataRequest.Builder()
                weatherDataRequestBuilder.add(coordinate, weatherProvider.majorWeatherEntityTypes.toTypedArray(), weatherProvider)

                val request = weatherDataRequestBuilder.build()
                val entity = when (val result = getWeatherDataUseCase(request.first(), false)) {
                    is WeatherResponseState.Success -> result.entity
                    is WeatherResponseState.Failure -> {
                        return@withContext WeatherContentUiState.Error(FailedReason.SERVER_ERROR)
                    }
                }

                val requestDateTime = entity.responseTime
                val dayNightCalculator = DayNightCalculator(coordinate.latitude, coordinate.longitude, requestDateTime.toTimeZone())

                val currentWeatherEntity = entity.toEntity<CurrentWeatherEntity>()
                val hourlyForecastEntity = entity.toEntity<HourlyForecastEntity>()
                val dailyForecastEntity = entity.toEntity<DailyForecastEntity>()

                val yesterdayWeather = if (entity.weatherDataMajorCategories.contains(MajorWeatherEntityType.YESTERDAY_WEATHER)) {
                    entity.toEntity<YesterdayWeatherEntity>()
                } else {
                    null
                }

                val currentWeather =
                    createCurrentWeatherUiModel(currentWeatherEntity, yesterdayWeather, dayNightCalculator, requestDateTime.toCalendar())
                val simpleHourlyForecast = HourlyForecastModelMapper.mapTo(hourlyForecastEntity, units, dayNightCalculator)
                val detailHourlyForecast = DetailHourlyForecastModelMapper.mapTo(hourlyForecastEntity, units, dayNightCalculator)
                val simpleDailyForecast = DailyForecastModelMapper.mapTo(dailyForecastEntity, units, dayNightCalculator)
                val detailDailyForecast = DetailDailyForecast(dailyForecastEntity, units)

                val weather = Weather(
                    currentWeather,
                    simpleHourlyForecast,
                    detailHourlyForecast,
                    simpleDailyForecast,
                    detailDailyForecast,
                    coordinate.latitude,
                    coordinate.longitude,
                    requestDateTime,
                )

                val allModel = WeatherSummaryPrompt.Model(
                    coordinate.latitude to coordinate.longitude,
                    requestDateTime,
                    args.weatherProvider,
                    currentWeatherEntity,
                    hourlyForecastEntity,
                    dailyForecastEntity,
                )
                WeatherContentUiState.Success(
                    args,
                    weather,
                    requestDateTime,
                    allModel,
                    units,
                )
            }
            mutableUiState.emit(newState)
        }
    }

    private fun createCurrentWeatherUiModel(
        currentWeatherEntity: CurrentWeatherEntity,
        yesterdayWeatherEntity: YesterdayWeatherEntity?,
        dayNightCalculator: DayNightCalculator,
        currentCalendar: Calendar,
    ): CurrentWeather {
        return currentWeatherEntity.run {
            val unit = units
            CurrentWeather(
                weatherCondition = weatherCondition,
                temperature = temperature.convertUnit(unit.temperatureUnit),
                feelsLikeTemperature = feelsLikeTemperature.convertUnit(unit.temperatureUnit),
                humidity = humidity,
                windSpeed = windSpeed.convertUnit(unit.windSpeedUnit),
                windDirection = windDirection,
                yesterdayTemperature = yesterdayWeatherEntity?.temperature?.convertUnit(unit.temperatureUnit),
                precipitationVolume = precipitationVolume.convertUnit(unit.precipitationUnit),
                dayNightCalculator = dayNightCalculator,
                currentCalendar = currentCalendar,
            )
        }
    }


}