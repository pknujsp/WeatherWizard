package io.github.pknujsp.weatherwizard.feature.weather.info


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.common.util.DayNightCalculator
import io.github.pknujsp.weatherwizard.core.common.util.toCalendar
import io.github.pknujsp.weatherwizard.core.common.util.toTimeZone
import io.github.pknujsp.weatherwizard.core.data.nominatim.NominatimRepository
import io.github.pknujsp.weatherwizard.core.domain.weather.GetAllWeatherDataUseCase
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.flickr.FlickrRequestParameters
import io.github.pknujsp.weatherwizard.core.model.nominatim.ReverseGeoCode
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeather
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeatherEntity
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecastEntity
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeather
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeatherEntity
import io.github.pknujsp.weatherwizard.core.ui.weather.item.DynamicDateTimeUiCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val getAllWeatherDataUseCase: GetAllWeatherDataUseCase,
    private val nominatimRepository: NominatimRepository,
) : ViewModel() {

    private val _weatherDataState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val weatherDataState: StateFlow<UiState<Unit>> = _weatherDataState

    private val _reverseGeoCode = MutableStateFlow<UiState<ReverseGeoCode>>(UiState.Loading)
    val reverseGeoCode: StateFlow<UiState<ReverseGeoCode>> = _reverseGeoCode

    private val _flickrRequestParameter = MutableStateFlow<FlickrRequestParameters?>(null)
    val flickrRequestParameter: StateFlow<FlickrRequestParameters?> = _flickrRequestParameter

    private val _currentWeather = MutableStateFlow<UiState<CurrentWeather>>(UiState.Loading)
    val currentWeather: StateFlow<UiState<CurrentWeather>> = _currentWeather

    private val _hourlyForecast = MutableStateFlow<UiState<HourlyForecast>>(UiState.Loading)
    val hourlyForecast: StateFlow<UiState<HourlyForecast>> = _hourlyForecast

    private val _dailyForecast = MutableStateFlow<UiState<DailyForecast>>(UiState.Loading)
    val dailyForecast: StateFlow<UiState<DailyForecast>> = _dailyForecast

    private val _yesterdayWeather = MutableStateFlow<UiState<YesterdayWeather>>(UiState.Loading)
    val yesterdayWeather: StateFlow<UiState<YesterdayWeather>> = _yesterdayWeather

    fun loadAllWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            val latitude = 35.236323256911774
            val longitude = 128.86341167027018
            val weatherDataProvider = WeatherDataProvider.Kma
            val requestDateTime = ZonedDateTime.now()

            reverseGeoCode(latitude, longitude)

            getAllWeatherDataUseCase(latitude,
                longitude,
                weatherDataProvider,
                requestId = System.nanoTime()).onSuccess { allWeatherDataEntity ->
                val dayNightCalculator = DayNightCalculator(latitude, longitude, requestDateTime.toTimeZone())
                val currentCalendar = requestDateTime.toCalendar()

                createFlickrRequestParameter(allWeatherDataEntity.currentWeatherEntity.weatherCondition.value, latitude, longitude,
                    requestDateTime)
                createCurrentWeatherUiModel(allWeatherDataEntity.currentWeatherEntity, dayNightCalculator, currentCalendar)
                createHourlyForecastUiModel(allWeatherDataEntity.hourlyForecastEntity, dayNightCalculator)
                createDailyForecastUiModel(allWeatherDataEntity.dailyForecastEntity)
                createYesterdayWeatherUiModel(allWeatherDataEntity.yesterdayWeatherEntity)

                _weatherDataState.value = UiState.Success(Unit)
            }.onFailure {
                _weatherDataState.value = UiState.Error(it)
            }
        }
    }

    private fun reverseGeoCode(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            nominatimRepository.reverseGeoCode(latitude, longitude).onSuccess {
                _reverseGeoCode.value = UiState.Success(ReverseGeoCode(
                    displayName = it.simpleDisplayName,
                    country = it.country,
                    countryCode = it.countryCode,
                    latitude = it.latitude,
                    longitude = it.longitude,
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
            val currentWeather = currentWeatherEntity.run {
                CurrentWeather(weatherCondition = weatherCondition,
                    temperature = temperature,
                    feelsLikeTemperature = feelsLikeTemperature,
                    humidity = humidity,
                    windSpeed = windSpeed,
                    windDirection = windDirection,
                    precipitationVolume = precipitationVolume,
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
            val hourlyForecast = hourlyForecastEntity.items.map {
                HourlyForecast.Item(
                    dateTime = it.dateTime,
                    weatherCondition = it.weatherCondition,
                    temperature = it.temperature,
                    feelsLikeTemperature = it.feelsLikeTemperature,
                    humidity = it.humidity,
                    windSpeed = it.windSpeed,
                    windDirection = it.windDirection,
                    precipitationVolume = it.precipitationVolume,
                    precipitationProbability = it.precipitationProbability,
                    dayNightCalculator = dayNightCalculator,
                )
            }

            val dateItems =
                DynamicDateTimeUiCreator(hourlyForecastEntity.items.map { it.dateTime.value }, HourlyForecast.itemWidth).invoke()
            _hourlyForecast.value = UiState.Success(HourlyForecast(hourlyForecast, dateItems))
        }
    }

    private fun createDailyForecastUiModel(
        dailyForecastEntity: DailyForecastEntity
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val dailyForecast = dailyForecastEntity.items.let { items ->
                val listMap = mutableMapOf<LocalDate, DailyForecast.DayItem>()
                items.forEach { item ->
                    val date = ZonedDateTime.parse(item.dateTime.value).toLocalDate()

                    if (!listMap.containsKey(date)) {
                        listMap[date] = DailyForecast.DayItem()
                    }
                    listMap[date]?.addValue(dateTime = item.dateTime,
                        weatherCondition = item.weatherCondition,
                        precipitationProbability = item.precipitationProbability,
                        minTemperature = item.minTemperature,
                        maxTemperature = item.maxTemperature)
                }

                listMap.toList().sortedBy { it.first }.map { it.second }
            }

            _dailyForecast.value = UiState.Success(DailyForecast(dailyForecast))
        }
    }

    private fun createYesterdayWeatherUiModel(
        yesterdayWeatherEntity: YesterdayWeatherEntity
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            _yesterdayWeather.value = UiState.Success(YesterdayWeather(temperature = yesterdayWeatherEntity.temperature))
        }
    }

    private fun createFlickrRequestParameter(
        weatherCondition: WeatherConditionCategory, latitude: Double, longitude: Double, requestDateTime: ZonedDateTime
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            _flickrRequestParameter.value = FlickrRequestParameters(
                weatherCondition = weatherCondition, latitude = latitude,
                longitude = longitude, refreshDateTime = requestDateTime,
            )
        }
    }
}