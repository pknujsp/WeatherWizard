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
import io.github.pknujsp.weatherwizard.core.model.WeatherInfo
import io.github.pknujsp.weatherwizard.core.model.flickr.FlickrRequestParameters
import io.github.pknujsp.weatherwizard.core.model.nominatim.ReverseGeoCode
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeather
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val getAllWeatherDataUseCase: GetAllWeatherDataUseCase,
    private val nominatimRepository: NominatimRepository,
) : ViewModel() {

    private val _weatherInfo = MutableStateFlow<UiState<WeatherInfo>>(UiState.Loading)
    val weatherInfo: StateFlow<UiState<WeatherInfo>> = _weatherInfo

    private val _reverseGeoCode = MutableStateFlow<UiState<ReverseGeoCode>>(UiState.Loading)
    val reverseGeoCode: StateFlow<UiState<ReverseGeoCode>> = _reverseGeoCode

    private val _flickrRequestParameter = MutableStateFlow<FlickrRequestParameters?>(null)

    val flickrRequestParameter: StateFlow<FlickrRequestParameters?> = _flickrRequestParameter.asStateFlow()

    fun loadAllWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            val latitude = 35.236323256911774
            val longitude = 128.86341167027018
            val weatherDataProvider = WeatherDataProvider.Kma
            val requestDateTime = ZonedDateTime.now()

            reverseGeoCode(latitude, longitude)

            val result = getAllWeatherDataUseCase(latitude, longitude, weatherDataProvider,
                requestId = System.nanoTime())

            result.onSuccess { allWeatherDataEntity ->
                val dayNightCalculator = DayNightCalculator(latitude, longitude, requestDateTime.toTimeZone())
                val currentCalendar = requestDateTime.toCalendar()

                val currentWeather = allWeatherDataEntity.currentWeatherEntity.run {
                    CurrentWeather(weatherCondition = weatherCondition,
                        temperature = temperature,
                        feelsLikeTemperature = feelsLikeTemperature,
                        humidity = humidity,
                        windSpeed = windSpeed,
                        windDirection = windDirection,
                        precipitationVolume = precipitationVolume,
                        dayNightCalculator = dayNightCalculator,
                        currentCalendar = currentCalendar
                    )
                }

                val hourlyForecast = allWeatherDataEntity.hourlyForecastEntity.items.map {
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
                        dayNightCalculator = dayNightCalculator, )
                }

                val dailyForecast = allWeatherDataEntity.dailyForecastEntity.items.let { items ->
                    val listMap = mutableMapOf<LocalDate, DailyForecast.DayItem>()
                    items.forEach { item ->
                        val date = ZonedDateTime.parse(item.dateTime.value).toLocalDate()

                        if (!listMap.containsKey(date)) {
                            listMap[date] = DailyForecast.DayItem()
                        }
                        listMap[date]?.addValue(
                            dateTime = item.dateTime,
                            weatherCondition = item.weatherCondition,
                            precipitationProbability = item.precipitationProbability,
                            minTemperature = item.minTemperature,
                            maxTemperature = item.maxTemperature
                        )
                    }

                    listMap.toList().sortedBy { it.first }.map { it.second }
                }

                val yesterdayWeather = allWeatherDataEntity.yesterdayWeatherEntity.run {
                    YesterdayWeather(
                        temperature = temperature
                    )
                }

                val weatherInfo = WeatherInfo(
                    currentWeather = currentWeather,
                    hourlyForecast = HourlyForecast(hourlyForecast),
                    dailyForecast = DailyForecast(dailyForecast),
                    yesterdayWeather = yesterdayWeather,
                )

                val flickrRequestParameter = FlickrRequestParameters(
                    weatherCondition = currentWeather.weatherCondition.value, latitude = latitude,
                    longitude = longitude, zoneId = ZoneId.systemDefault(), refreshDateTime = requestDateTime,
                )

                _flickrRequestParameter.value = flickrRequestParameter
                _weatherInfo.value = UiState.Success(weatherInfo)
            }.onFailure {
                _weatherInfo.value = UiState.Error(it)
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
}