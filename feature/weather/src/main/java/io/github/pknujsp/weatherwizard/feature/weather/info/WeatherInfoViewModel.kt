package io.github.pknujsp.weatherwizard.feature.weather.info


import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.common.util.toTimeZone
import io.github.pknujsp.weatherwizard.core.data.favorite.FavoriteAreaListRepository
import io.github.pknujsp.weatherwizard.core.data.favorite.TargetAreaRepository
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.data.settings.SettingsRepository
import io.github.pknujsp.weatherwizard.core.domain.weather.GetAllWeatherDataUseCase
import io.github.pknujsp.weatherwizard.core.model.ProcessState
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.favorite.TargetAreaType
import io.github.pknujsp.weatherwizard.core.model.flickr.FlickrRequestParameters
import io.github.pknujsp.weatherwizard.core.model.nominatim.ReverseGeoCode
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.model.weather.common.PrecipitationValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueType
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val getAllWeatherDataUseCase: GetAllWeatherDataUseCase,
    private val nominatimRepository: NominatimRepository,
    private val targetAreaRepository: TargetAreaRepository,
    private val favoriteAreaListRepository: FavoriteAreaListRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Idle)
    val processState: StateFlow<ProcessState> = _processState

    private val _args = MutableStateFlow<RequestWeatherDataArgs?>(null)
    val args: StateFlow<RequestWeatherDataArgs?> = _args

    private val _targetAreaType = MutableStateFlow<TargetAreaType?>(null)
    val targetAreaType: StateFlow<TargetAreaType?> = _targetAreaType

    private val _reverseGeoCode = MutableStateFlow<UiState<ReverseGeoCode>>(UiState.Loading)
    val reverseGeoCode: StateFlow<UiState<ReverseGeoCode>> = _reverseGeoCode

    private val _flickrRequestParameter = MutableStateFlow<UiState<FlickrRequestParameters>>(UiState.Loading)
    val flickrRequestParameter: StateFlow<UiState<FlickrRequestParameters>> = _flickrRequestParameter

    private val _currentWeather = MutableStateFlow<UiState<CurrentWeather>>(UiState.Loading)
    val currentWeather: StateFlow<UiState<CurrentWeather>> = _currentWeather

    private val _simpleHourlyForecast = MutableStateFlow<UiState<SimpleHourlyForecast>>(UiState.Loading)
    val simpleHourlyForecast: StateFlow<UiState<SimpleHourlyForecast>> = _simpleHourlyForecast

    private val _detailHourlyForecast = MutableStateFlow<UiState<DetailHourlyForecast>>(UiState.Loading)
    val detailHourlyForecast: StateFlow<UiState<DetailHourlyForecast>> = _detailHourlyForecast

    private val _simpleDailyForecast = MutableStateFlow<UiState<SimpleDailyForecast>>(UiState.Loading)
    val simpleDailyForecast: StateFlow<UiState<SimpleDailyForecast>> = _simpleDailyForecast

    private val _detailDailyForecast = MutableStateFlow<UiState<DetailDailyForecast>>(UiState.Loading)
    val detailDailyForecast: StateFlow<UiState<DetailDailyForecast>> = _detailDailyForecast

    private val _yesterdayWeather = MutableStateFlow<UiState<YesterdayWeather>>(UiState.Loading)
    val yesterdayWeather: StateFlow<UiState<YesterdayWeather>> = _yesterdayWeather

    init {
        viewModelScope.launch {
            settingsRepository.init()
            _targetAreaType.value = targetAreaRepository.getTargetArea()
        }
    }

    fun waitForLoad(){
        viewModelScope.launch {
            _processState.value = ProcessState.Idle
        }
    }

    fun setArgsAndLoad(location: Location? = null) {
        viewModelScope.launch {
            _processState.value = ProcessState.Running
            targetAreaType.value?.let { target ->
                val (lat, lon) = if (target is TargetAreaType.CurrentLocation) {
                    location!!.latitude to location.longitude
                } else {
                    favoriteAreaListRepository.getById(target.id).getOrThrow().run {
                        latitude to longitude
                    }
                }

                _args.value =
                    RequestWeatherDataArgs(latitude = lat,
                        longitude = lon,
                        weatherDataProvider = settingsRepository.getWeatherDataProvider(),
                        targetAreaType = target)

                loadAllWeatherData()
            }
        }
    }

    fun updateWeatherDataProvider(weatherDataProvider: WeatherDataProvider) {
        viewModelScope.launch {
            _processState.value = ProcessState.Running
            settingsRepository.setWeatherDataProvider(weatherDataProvider)
        }
    }

    private fun loadAllWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            args.value?.run {
                val requestDateTime = ZonedDateTime.now()
                reverseGeoCode(latitude, longitude, requestDateTime)

                val dayNightCalculator = DayNightCalculator(latitude, longitude, requestDateTime.toTimeZone())
                val currentCalendar = requestDateTime.toCalendar()

                getAllWeatherDataUseCase(latitude,
                    longitude,
                    weatherDataProvider,
                    requestDateTime.toInstant().toEpochMilli()).onSuccess { allWeatherDataEntity ->
                    createFlickrRequestParameter(allWeatherDataEntity.currentWeatherEntity.weatherCondition.value,
                        latitude,
                        longitude,
                        requestDateTime)
                    createCurrentWeatherUiModel(allWeatherDataEntity.currentWeatherEntity, dayNightCalculator, currentCalendar)
                    createHourlyForecastUiModel(allWeatherDataEntity.hourlyForecastEntity, dayNightCalculator)
                    createDailyForecastUiModel(allWeatherDataEntity.dailyForecastEntity)
                    allWeatherDataEntity.yesterdayWeatherEntity?.run {
                        createYesterdayWeatherUiModel(this)
                    }

                    _processState.value = ProcessState.Succeed
                }.onFailure {
                    _processState.value = ProcessState.Failed
                }
            }
        }
    }

    private fun reverseGeoCode(latitude: Double, longitude: Double, requestDateTime: ZonedDateTime) {
        viewModelScope.launch(Dispatchers.IO) {
            nominatimRepository.reverseGeoCode(latitude, longitude).onSuccess {
                _reverseGeoCode.value = UiState.Success(ReverseGeoCode(
                    displayName = it.simpleDisplayName,
                    country = it.country,
                    countryCode = it.countryCode,
                    latitude = it.latitude,
                    longitude = it.longitude,
                    requestDateTime = requestDateTime.format(DateTimeFormatter.ofPattern("M.d EEE HH:mm")),
                ))
            }.onFailure {
                _reverseGeoCode.value = UiState.Error(it)
            }
        }
    }


    private fun createCurrentWeatherUiModel(
        currentWeatherEntity: CurrentWeatherEntity, dayNightCalculator: DayNightCalculator, currentCalendar: java.util.Calendar
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            _currentWeather.value = UiState.Loading
            val currentWeather = currentWeatherEntity.run {
                val unit = settingsRepository.currentUnits.value
                CurrentWeather(weatherCondition = weatherCondition,
                    temperature = TemperatureValueType(temperature.convertUnit(unit.temperatureUnit),
                        unit.temperatureUnit),
                    feelsLikeTemperature = TemperatureValueType(feelsLikeTemperature.convertUnit(unit.temperatureUnit),
                        unit.temperatureUnit),
                    humidity = humidity,
                    windSpeed = WindSpeedValueType(windSpeed.convertUnit(unit.windSpeedUnit), unit.windSpeedUnit),
                    windDirection = windDirection,
                    precipitationVolume = PrecipitationValueType(precipitationVolume.convertUnit(
                        unit.precipitationUnit),
                        unit.precipitationUnit),
                    dayNightCalculator = dayNightCalculator,
                    currentCalendar = currentCalendar)
            }

            _currentWeather.value = UiState.Success(currentWeather)
        }
    }

    private fun createHourlyForecastUiModel(
        hourlyForecastEntity: HourlyForecastEntity, dayNightCalculator: DayNightCalculator
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val unit = settingsRepository.currentUnits.value

            val simpleHourlyForecast = hourlyForecastEntity.items.mapIndexed { i, it ->
                SimpleHourlyForecast.Item(
                    id = i,
                    dateTime = it.dateTime,
                    weatherCondition = it.weatherCondition,
                    temperature = TemperatureValueType(it.temperature.convertUnit(unit.temperatureUnit),
                        unit.temperatureUnit),
                    feelsLikeTemperature = TemperatureValueType(it.feelsLikeTemperature.convertUnit(
                        unit.temperatureUnit),
                        unit.temperatureUnit),
                    humidity = it.humidity,
                    windSpeed = WindSpeedValueType(it.windSpeed.convertUnit(unit.windSpeedUnit), unit.windSpeedUnit),
                    windDirection = it.windDirection,
                    precipitationVolume = PrecipitationValueType(it.precipitationVolume.convertUnit(
                        unit.precipitationUnit),
                        unit.precipitationUnit),
                    precipitationProbability = it.precipitationProbability,
                    dayNightCalculator = dayNightCalculator,
                )
            }

            val formatter = DateTimeFormatter.ofPattern("M.d EEE")
            val detailHourlyForecast = hourlyForecastEntity.items.groupBy { ZonedDateTime.parse(it.dateTime.value).dayOfYear }.map {
                ZonedDateTime.parse(it.value.first().dateTime.value).format(formatter) to it.value.mapIndexed { i, item ->
                    DetailHourlyForecast.Item(
                        id = i,
                        dateTime = item.dateTime,
                        weatherCondition = item.weatherCondition,
                        temperature = TemperatureValueType(item.temperature.convertUnit(unit.temperatureUnit),
                            unit.temperatureUnit),
                        feelsLikeTemperature = TemperatureValueType(item.feelsLikeTemperature.convertUnit(
                            unit.temperatureUnit),
                            unit.temperatureUnit),
                        humidity = item.humidity,
                        windSpeed = WindSpeedValueType(item.windSpeed.convertUnit(unit.windSpeedUnit), unit.windSpeedUnit),
                        windDirection = item.windDirection,
                        precipitationVolume = PrecipitationValueType(item.precipitationVolume.convertUnit(
                            unit.precipitationUnit),
                            unit.precipitationUnit),
                        precipitationProbability = item.precipitationProbability,
                        dayNightCalculator = dayNightCalculator,
                    )
                }
            }

            val dateItems =
                DynamicDateTimeUiCreator(hourlyForecastEntity.items.map { it.dateTime.value }, SimpleHourlyForecast.itemWidth).invoke()
            _simpleHourlyForecast.value = UiState.Success(SimpleHourlyForecast(simpleHourlyForecast, dateItems))
            _detailHourlyForecast.value = UiState.Success(DetailHourlyForecast(detailHourlyForecast))
        }
    }

    private fun createDailyForecastUiModel(
        dailyForecastEntity: DailyForecastEntity
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            _simpleDailyForecast.value = UiState.Success(SimpleDailyForecast(dailyForecastEntity, settingsRepository.currentUnits.value))
            _detailDailyForecast.value = UiState.Success(DetailDailyForecast(dailyForecastEntity, settingsRepository.currentUnits.value))
        }
    }

    private fun createYesterdayWeatherUiModel(
        yesterdayWeatherEntity: YesterdayWeatherEntity
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val temperatureUnit = settingsRepository.currentUnits.value.temperatureUnit
            _yesterdayWeather.value =
                UiState.Success(YesterdayWeather(temperature = TemperatureValueType(yesterdayWeatherEntity.temperature.convertUnit(
                    temperatureUnit), temperatureUnit)))
        }
    }

    private fun createFlickrRequestParameter(
        weatherCondition: WeatherConditionCategory, latitude: Double, longitude: Double, requestDateTime: ZonedDateTime
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            _flickrRequestParameter.value = UiState.Success(FlickrRequestParameters(
                weatherCondition = weatherCondition,
                latitude = latitude,
                longitude = longitude,
                refreshDateTime = requestDateTime,
            ))
        }
    }
}