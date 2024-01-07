package io.github.pknujsp.weatherwizard.feature.weather.info


import android.util.Log
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
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.domain.location.CurrentLocationResultState
import io.github.pknujsp.weatherwizard.core.domain.location.GetCurrentLocationUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.GetWeatherDataUseCase
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherDataRequest
import io.github.pknujsp.weatherwizard.core.domain.weather.WeatherResponseState
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val favoriteAreaListRepository: FavoriteAreaListRepository,
    private val settingsRepository: SettingsRepository,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val nominatimRepository: NominatimRepository,
    private val getWeatherDataUseCase: GetWeatherDataUseCase,
    @CoDispatcher(CoDispatcherType.IO) private val dispatcher: CoroutineDispatcher,
    targetLocationRepository: TargetLocationRepository,
) : ViewModel() {
    private var job: Job? = null

    var isLoading: Boolean by mutableStateOf(true)
        private set

    private val mutableUiState = MutableStateFlow<WeatherContentUiState?>(null)

    val uiState = mutableUiState.asStateFlow().filterNotNull().onEach {
        Log.d("WeatherInfoViewModel", "uiState 흐름: $it")
        isLoading = false
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val baseArguments = targetLocationRepository.targetLocation.map { location ->
        Log.d("WeatherInfoViewModel", "baseArgumentsFlow 흐름: targetLocation $location")
        createLocationTypeModel(location)
    }.flowOn(dispatcher).filterNotNull().zip(settingsRepository.settings) { location, settings ->
        Log.d("WeatherInfoViewModel", "baseArgumentsFlow 흐름: settings $settings")
        RequestWeatherArguments(settings.weatherProvider, location)
    }.flowOn(dispatcher).onEach {
        Log.d("WeatherInfoViewModel", "baseArgumentsFlow 흐름: 날씨 데이터 로드 $it")
        loadAllWeatherData(it)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            loadAllWeatherData(baseArguments.value!!)
        }
    }

    private suspend fun createLocationTypeModel(location: SelectedLocationModel): LocationTypeModel? {
        val locationTypeModel = viewModelScope.async {
            isLoading = true
            createLocationModel(location)
        }
        return locationTypeModel.await()
    }

    private suspend fun createLocationModel(targetLocation: SelectedLocationModel): LocationTypeModel? {
        val result = withContext(dispatcher) {
            if (targetLocation.locationType is LocationType.CurrentLocation) {
                loadCurrentLocation()
            } else {
                loadFavoriteLocation(targetLocation.locationId)
            }
        }

        return result.first ?: run {
            mutableUiState.value = WeatherContentUiState.Error(result.second!!)
            null
        }
    }

    private suspend fun loadCurrentLocation(): Pair<LocationTypeModel?, FailedReason?> {
        return when (val currentLocation = getCurrentLocationUseCase()) {
            is CurrentLocationResultState.Success -> {
                LocationTypeModel(
                    locationType = LocationType.CurrentLocation,
                    latitude = currentLocation.latitude,
                    longitude = currentLocation.longitude,
                ) to null
            }

            is CurrentLocationResultState.Failure -> {
                null to currentLocation.reason
            }
        }
    }

    private suspend fun loadFavoriteLocation(locationId: Long): Pair<LocationTypeModel?, FailedReason?> {
        return favoriteAreaListRepository.getById(locationId).map {
            LocationTypeModel(
                locationType = LocationType.CustomLocation,
                latitude = it.latitude,
                longitude = it.longitude,
                address = it.areaName,
                country = it.countryName,
            )
        }.fold(onSuccess = { location ->
            location to null
        }, onFailure = {
            null to FailedReason.UNKNOWN
        })
    }

    fun cancelLoading() {
        viewModelScope.launch {
            if (isLoading) {
                job?.cancel()
                mutableUiState.value = WeatherContentUiState.Error(FailedReason.CANCELED)
            }
        }
    }

    private suspend fun reverseGeoCode(location: SelectedLocationModel){
        nominatimRepository.reverseGeoCode(currentLocation.latitude, currentLocation.longitude).map {
            LocationTypeModel(
                locationType = LocationType.CurrentLocation,
                latitude = currentLocation.latitude,
                longitude = currentLocation.longitude,
                address = it.simpleDisplayName,
                country = it.country,
            )
        }.fold(onSuccess = { location ->
            location to null
        }, onFailure = {
            null to FailedReason.REVERSE_GEOCODE_ERROR
        })
    }

    fun updateWeatherDataProvider(weatherProvider: WeatherProvider) {
        viewModelScope.launch {
            settingsRepository.update(WeatherProvider, weatherProvider)
        }
    }

    private suspend fun loadAllWeatherData(args: RequestWeatherArguments) {
        job?.cancel()
        job = viewModelScope.launch {
            val newState = withContext(dispatcher) {
                val weatherProvider = args.weatherProvider
                val location = args.location

                val weatherDataRequest = WeatherDataRequest()
                weatherDataRequest.addRequest(location, weatherProvider.majorWeatherEntityTypes, weatherProvider)

                val entity = when (val result = getWeatherDataUseCase(weatherDataRequest.finalRequests[0], false)) {
                    is WeatherResponseState.Success -> result.entity
                    is WeatherResponseState.Failure -> {
                        return@withContext WeatherContentUiState.Error(FailedReason.SERVER_ERROR)
                    }
                }

                val requestDateTime = ZonedDateTime.now()
                val dayNightCalculator = DayNightCalculator(location.latitude, location.longitude, requestDateTime.toTimeZone())

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
                    location.latitude,
                    location.longitude,
                    requestDateTime)
                WeatherContentUiState.Success(args, weather, requestDateTime)
            }
            mutableUiState.value = newState
        }
    }


    private fun createCurrentWeatherUiModel(
        currentWeatherEntity: CurrentWeatherEntity, dayNightCalculator: DayNightCalculator, currentCalendar: Calendar
    ): CurrentWeather {
        return currentWeatherEntity.run {
            val unit = settingsRepository.settings.value.units
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
        val unit = settingsRepository.settings.value.units
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
        val unit = settingsRepository.settings.value.units
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
    ) = SimpleDailyForecast(dailyForecastEntity, settingsRepository.settings.value.units)

    private fun createDetailDailyForecastUiModel(
        dailyForecastEntity: DailyForecastEntity
    ) = DetailDailyForecast(dailyForecastEntity, settingsRepository.settings.value.units)

    private fun createYesterdayWeatherUiModel(
        yesterdayWeatherEntity: YesterdayWeatherEntity
    ): YesterdayWeather {
        val temperatureUnit = settingsRepository.settings.value.units.temperatureUnit
        return YesterdayWeather(temperature = yesterdayWeatherEntity.temperature.convertUnit(temperatureUnit))
    }
}