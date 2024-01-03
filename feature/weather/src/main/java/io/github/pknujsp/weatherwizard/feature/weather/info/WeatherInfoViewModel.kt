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
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

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

    var uiState: WeatherContentUiState by mutableStateOf(WeatherContentUiState.Loading)
        private set

    fun initialize() {
        viewModelScope.launch(dispatcher) {
            uiState = WeatherContentUiState.Loading
            val location = targetLocationRepository.getTargetLocation()
            val weatherProvider = settingsRepository.settings.value.weatherProvider
            Log.d("initialize", "initialize: $location, $weatherProvider")

            if (location.locationType is LocationType.CurrentLocation) {
                when (val currentLocation = getCurrentLocationUseCase()) {
                    is CurrentLocationResultState.Success -> {
                        nominatimRepository.reverseGeoCode(currentLocation.latitude, currentLocation.longitude).onSuccess {
                            val args = RequestWeatherArguments(weatherProvider = weatherProvider,
                                location = LocationTypeModel(
                                    locationType = LocationType.CurrentLocation,
                                    latitude = currentLocation.latitude,
                                    longitude = currentLocation.longitude,
                                    address = it.simpleDisplayName,
                                    country = it.country,
                                ))
                            loadAllWeatherData(args)
                        }.onFailure {
                            uiState = WeatherContentUiState.Error(FailedReason.SERVER_ERROR)
                        }
                    }

                    is CurrentLocationResultState.Failure -> {
                        uiState = WeatherContentUiState.Error(currentLocation.reason)
                    }

                }
            } else {
                val targetLocation = favoriteAreaListRepository.getById(location.locationId)
                targetLocation.onFailure {
                    uiState = WeatherContentUiState.Error(FailedReason.UNKNOWN)
                }.onSuccess {
                    val args = RequestWeatherArguments(weatherProvider = settingsRepository.settings.value.weatherProvider,
                        location = LocationTypeModel(
                            locationType = LocationType.CustomLocation,
                            latitude = it.latitude,
                            longitude = it.longitude,
                            address = it.areaName,
                            country = it.countryName,
                        ))
                    loadAllWeatherData(args)
                }

            }
        }
    }


    fun updateWeatherDataProvider(weatherProvider: WeatherProvider) {
        viewModelScope.launch {
            Log.d("updateWeatherDataProvider", "updateWeatherDataProvider: $weatherProvider")
            settingsRepository.update(WeatherProvider, weatherProvider)
            initialize()
        }
    }

    private fun loadAllWeatherData(args: RequestWeatherArguments) {
        viewModelScope.launch(dispatcher) {
            args.run {
                val weatherDataRequest = WeatherDataRequest()
                weatherDataRequest.addRequest(location, weatherProvider.majorWeatherEntityTypes, weatherProvider)

                val entity = when (val result = getWeatherDataUseCase(weatherDataRequest.finalRequests[0], false)) {
                    is WeatherResponseState.Success -> result.entity
                    is WeatherResponseState.Failure -> {
                        uiState = WeatherContentUiState.Error(FailedReason.SERVER_ERROR)
                        return@launch
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
                uiState = WeatherContentUiState.Success(args, weather, requestDateTime)
            }
        }
    }


    private fun createCurrentWeatherUiModel(
        currentWeatherEntity: CurrentWeatherEntity, dayNightCalculator: DayNightCalculator, currentCalendar: java.util.Calendar
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