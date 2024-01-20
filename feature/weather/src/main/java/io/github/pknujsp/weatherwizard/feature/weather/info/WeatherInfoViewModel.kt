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
import io.github.pknujsp.weatherwizard.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.weatherwizard.core.data.favorite.TargetLocationRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.domain.location.CurrentLocationState
import io.github.pknujsp.weatherwizard.core.domain.location.GetCurrentLocationCoordinate
import io.github.pknujsp.weatherwizard.core.domain.weather.GetWeatherDataUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.weatherwizard.core.model.weather.common.MajorWeatherEntityType
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
import io.github.pknujsp.weatherwizard.feature.weather.info.geocode.TargetLocationModel
import io.github.pknujsp.weatherwizard.feature.weather.summary.WeatherDataParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val getCurrentLocationUseCase: GetCurrentLocationCoordinate,
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val favoriteAreaListRepository: FavoriteAreaListRepository,
    private val targetLocationRepository: TargetLocationRepository,
    @CoDispatcher(CoDispatcherType.IO) private val dispatcher: CoroutineDispatcher,
) : ViewModel() {
    private var loadWeatherDataJob: Job? = null
    private var targetLocationJob: Job? = null

    private val units get() = settingsRepository.settings.replayCache.last().units

    var isLoading: Boolean by mutableStateOf(true)
        private set

    private val mutableTargetLocations = MutableSharedFlow<TargetLocationModel?>(1, 0, BufferOverflow.DROP_OLDEST)
    val targetLocation = mutableTargetLocations.asSharedFlow()

    private val mutableUiState = MutableSharedFlow<WeatherContentUiState>(1, 0, BufferOverflow.DROP_OLDEST)

    val uiState = mutableUiState.asSharedFlow().onEach {
        isLoading = false
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val argumentsFlow = targetLocation.filterNotNull().combine(settingsRepository.settings) { location, settings ->
        RequestWeatherArguments(settings.weatherProvider, location.latitude, location.longitude)
    }.onEach {
        loadAllWeatherData(it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        targetLocationRepository.targetLocation.distinctUntilChanged().onEach { location ->
            createLocationTypeModel(location)
        }.launchIn(viewModelScope)
    }

    private suspend fun createLocationTypeModel(location: SelectedLocationModel) {
        targetLocationJob?.cancel()
        targetLocationJob = viewModelScope.launch {
            isLoading = true
            val result = withContext(dispatcher) {
                if (location.locationType is LocationType.CurrentLocation) {
                    loadCurrentLocation().first()
                } else {
                    val favoriteLocation = favoriteAreaListRepository.getById(location.locationId).getOrThrow()
                    TargetLocationModel(address = favoriteLocation.areaName,
                        country = favoriteLocation.countryName,
                        latitude = favoriteLocation.latitude,
                        longitude = favoriteLocation.longitude) to null
                }
            }

            result.first?.run {
                mutableTargetLocations.emit(this)
            } ?: run {
                mutableUiState.emit(WeatherContentUiState.Error(result.second!!))
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            createLocationTypeModel(targetLocationRepository.getCurrentTargetLocation())
        }
    }

    private suspend fun loadCurrentLocation() = callbackFlow {
        val now = LocalDateTime.now()
        getCurrentLocationUseCase(true)

        getCurrentLocationUseCase.currentLocationFlow.filterNotNull().filter {
            it.time >= now && it !is CurrentLocationState.Loading
        }.collect {
            when (it) {
                is CurrentLocationState.Success -> {
                    send(TargetLocationModel(latitude = it.latitude, longitude = it.longitude) to null)
                }

                is CurrentLocationState.Failure -> {
                    send(null to it.reason)
                }

                else -> {}
            }
            this.cancel()
        }
    }


    fun cancelLoading() {
        viewModelScope.launch {
            targetLocationJob?.cancel()
            loadWeatherDataJob?.cancel()
            isLoading = false
            mutableUiState.emit(WeatherContentUiState.Error(FailedReason.CANCELED))
        }
    }


    fun updateWeatherDataProvider(weatherProvider: WeatherProvider) {
        viewModelScope.launch {
            isLoading = true
            settingsRepository.update(WeatherProvider, weatherProvider)
        }
    }

    private suspend fun loadAllWeatherData(args: RequestWeatherArguments) {
        loadWeatherDataJob?.cancel()
        loadWeatherDataJob = viewModelScope.launch {
            val newState = withContext(dispatcher) {
                val weatherProvider = args.weatherProvider
                val coordinate = WeatherDataRequest.Coordinate(args.latitude, args.longitude)

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

                val weather = Weather(currentWeather,
                    simpleHourlyForecast,
                    detailHourlyForecast,
                    simpleDailyForecast,
                    detailDailyForecast,
                    yesterdayWeather,
                    coordinate.latitude,
                    coordinate.longitude,
                    requestDateTime)

                val allModel = WeatherDataParser.Model(
                    requestDateTime.toString(),
                    currentWeatherEntity,
                    hourlyForecastEntity,
                    dailyForecastEntity,
                )
                WeatherContentUiState.Success(args, weather, requestDateTime, allModel)
            }
            mutableUiState.emit(newState)
        }
    }


    private fun createCurrentWeatherUiModel(
        currentWeatherEntity: CurrentWeatherEntity, dayNightCalculator: DayNightCalculator, currentCalendar: Calendar
    ): CurrentWeather {
        return currentWeatherEntity.run {
            val unit = units
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
        val unit = units
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
        val unit = units
        var keyId = 0
        val formatter = DateTimeFormatter.ofPattern("M.d EEE")
        val detailHourlyForecast = hourlyForecastEntity.items.groupBy {
            ZonedDateTime.parse(it.dateTime.value).dayOfYear
        }.map { (_, items) ->
            DetailHourlyForecast.Header(id = keyId++,
                title = ZonedDateTime.parse(items.first().dateTime.value).format(formatter)) to items.map { item ->
                DetailHourlyForecast.Item(
                    id = keyId++,
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
    ) = SimpleDailyForecast(dailyForecastEntity, units)

    private fun createDetailDailyForecastUiModel(
        dailyForecastEntity: DailyForecastEntity
    ) = DetailDailyForecast(dailyForecastEntity, units)

    private fun createYesterdayWeatherUiModel(
        yesterdayWeatherEntity: YesterdayWeatherEntity
    ): YesterdayWeather {
        val temperatureUnit = units.temperatureUnit
        return YesterdayWeather(temperature = yesterdayWeatherEntity.temperature.convertUnit(temperatureUnit))
    }
}