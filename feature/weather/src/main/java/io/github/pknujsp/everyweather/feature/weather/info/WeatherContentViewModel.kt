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
import io.github.pknujsp.everyweather.core.model.weather.common.MajorWeatherEntityType
import io.github.pknujsp.everyweather.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.everyweather.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.everyweather.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.everyweather.core.model.weather.yesterday.YesterdayWeatherEntity
import io.github.pknujsp.everyweather.core.ui.weather.item.DynamicDateTimeUiCreator
import io.github.pknujsp.everyweather.feature.weather.info.currentweather.model.CurrentWeather
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model.DetailDailyForecast
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model.SimpleDailyForecast
import io.github.pknujsp.everyweather.feature.weather.info.geocode.TargetLocationModel
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.DetailHourlyForecast
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.SimpleHourlyForecast
import io.github.pknujsp.everyweather.feature.weather.summary.WeatherSummaryPrompt
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
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
class WeatherContentViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val getCurrentLocationUseCase: GetCurrentLocationCoordinate,
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    private val favoriteAreaListRepository: FavoriteAreaListRepository,
    @CoDispatcher(CoDispatcherType.IO) private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private var loadWeatherDataJob: Job? = null
    private var targetLocationJob: Job? = null

    var isLoading: Boolean by mutableStateOf(false)
        private set

    private val mutableTargetLocations = MutableSharedFlow<TargetLocationModel?>(1, 0, BufferOverflow.DROP_OLDEST)

    val targetLocation = mutableTargetLocations.filterNotNull().onEach { location ->
        loadAllWeatherData(RequestWeatherArguments(settingsRepository.settings.replayCache.last().weatherProvider,
            location.latitude,
            location.longitude))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val mutableUiState = MutableSharedFlow<WeatherContentUiState>(1, 0, BufferOverflow.DROP_OLDEST)

    val uiState = mutableUiState.asSharedFlow().onEach {
        isLoading = false
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun load(selectedLocationModel: SelectedLocationModel) {
        viewModelScope.launch {
            targetLocationJob?.cancel()
            loadWeatherDataJob?.cancel()

            targetLocationJob = viewModelScope.launch {
                isLoading = true
                val result = withContext(dispatcher) {
                    if (selectedLocationModel.locationType is LocationType.CurrentLocation) {
                        loadCurrentLocation().first()
                    } else {
                        val favoriteLocation = favoriteAreaListRepository.getById(selectedLocationModel.locationId).getOrThrow()
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


    fun cancel() {
        viewModelScope.launch {
            isLoading = false
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

                val yesterdayWeather = if (entity.weatherDataMajorCategories.contains(MajorWeatherEntityType.YESTERDAY_WEATHER)) {
                    entity.toEntity<YesterdayWeatherEntity>()
                } else {
                    null
                }

                val currentWeather =
                    createCurrentWeatherUiModel(currentWeatherEntity, yesterdayWeather, dayNightCalculator, requestDateTime.toCalendar())
                val simpleHourlyForecast = createSimpleHourlyForecastUiModel(hourlyForecastEntity, dayNightCalculator)
                val detailHourlyForecast = createDetailHourlyForecastUiModel(hourlyForecastEntity, dayNightCalculator)
                val simpleDailyForecast = createSimpleDailyForecastUiModel(dailyForecastEntity)
                val detailDailyForecast = createDetailDailyForecastUiModel(dailyForecastEntity)

                val weather = Weather(currentWeather,
                    simpleHourlyForecast,
                    detailHourlyForecast,
                    simpleDailyForecast,
                    detailDailyForecast,
                    coordinate.latitude,
                    coordinate.longitude,
                    requestDateTime)

                val allModel = WeatherSummaryPrompt.Model(
                    coordinate.latitude to coordinate.longitude,
                    requestDateTime,
                    args.weatherProvider,
                    currentWeatherEntity,
                    hourlyForecastEntity,
                    dailyForecastEntity,
                )
                WeatherContentUiState.Success(args,
                    weather,
                    requestDateTime,
                    allModel,
                    settingsRepository.settings.replayCache.last().units)
            }
            mutableUiState.emit(newState)
        }
    }


    private fun createCurrentWeatherUiModel(
        currentWeatherEntity: CurrentWeatherEntity,
        yesterdayWeatherEntity: YesterdayWeatherEntity?,
        dayNightCalculator: DayNightCalculator,
        currentCalendar: Calendar
    ): CurrentWeather {
        return currentWeatherEntity.run {
            val unit = settingsRepository.settings.replayCache.last().units
            CurrentWeather(weatherCondition = weatherCondition,
                temperature = temperature.convertUnit(unit.temperatureUnit),
                feelsLikeTemperature = feelsLikeTemperature.convertUnit(unit.temperatureUnit),
                humidity = humidity,
                windSpeed = windSpeed.convertUnit(unit.windSpeedUnit),
                windDirection = windDirection,
                yesterdayTemperature = yesterdayWeatherEntity?.temperature?.convertUnit(unit.temperatureUnit),
                precipitationVolume = precipitationVolume.convertUnit(unit.precipitationUnit),
                dayNightCalculator = dayNightCalculator,
                currentCalendar = currentCalendar)
        }
    }

    private fun createSimpleHourlyForecastUiModel(
        hourlyForecastEntity: HourlyForecastEntity, dayNightCalculator: DayNightCalculator
    ): SimpleHourlyForecast {
        val unit = settingsRepository.settings.replayCache.last().units

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
        val unit = settingsRepository.settings.replayCache.last().units
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
    ) = SimpleDailyForecast(dailyForecastEntity, settingsRepository.settings.replayCache.last().units)

    private fun createDetailDailyForecastUiModel(
        dailyForecastEntity: DailyForecastEntity
    ) = DetailDailyForecast(dailyForecastEntity, settingsRepository.settings.replayCache.last().units)
}