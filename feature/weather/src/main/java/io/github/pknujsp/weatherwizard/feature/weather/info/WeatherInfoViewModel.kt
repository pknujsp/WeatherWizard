package io.github.pknujsp.weatherwizard.feature.weather.info


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.domain.weather.GetAllWeatherDataUseCase
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.WeatherInfo
import io.github.pknujsp.weatherwizard.core.model.weather.common.AirQualityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.AirQualityUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.AirQualityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.HumidityValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.IntPercentUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionValueType
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueType
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeather
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WeatherInfoViewModel @Inject constructor(
    private val getAllWeatherDataUseCase: GetAllWeatherDataUseCase
) : ViewModel() {

    private val _weatherInfo = MutableStateFlow<UiState<WeatherInfo>>(UiState.Loading)
    val weatherInfo = _weatherInfo.asStateFlow()

    fun loadAllWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getAllWeatherDataUseCase(latitude = 35.236323256911774,
                longitude = 128.86341167027018,
                weatherDataProvider = WeatherDataProvider.Kma,
                requestId = System.nanoTime())

            result.onSuccess { allWeatherDataEntity ->
                val currentWeather = allWeatherDataEntity.currentWeatherEntity.run {
                    CurrentWeather(weatherCondition = WeatherConditionValueType(weatherIcon = WeatherConditionValueType.icon(
                        weatherCondition.value), weatherCondition = weatherCondition.value),
                        temperature = TemperatureValueType(temperature.current, TemperatureUnit.Celsius),
                        feelsLikeTemperature = TemperatureValueType(temperature.feelsLike, TemperatureUnit.Celsius),
                        humidity = HumidityValueType(humidity, IntPercentUnit()),
                        windSpeed = WindSpeedValueType(windEntity.speed, WindSpeedUnit.MeterPerSecond),
                        windDirection = WindDirectionValueType(windEntity.direction, WindDirectionUnit.Degree),
                        airQuality = AirQualityValueType(AirQualityType(0.0), AirQualityUnit.AQI))
                }

                val hourlyForecast = allWeatherDataEntity.hourlyForecastEntity.items.map {
                    HourlyForecast.Item(
                        dateTime = it.dateTime,
                        temperature = TemperatureValueType(it.temperature.current, TemperatureUnit.Celsius),
                        weatherCondition = WeatherConditionValueType(weatherIcon = WeatherConditionValueType.icon(it.weatherCondition.value),
                            weatherCondition = it.weatherCondition.value),
                        windSpeed = WindSpeedValueType(it.wind.speed, WindSpeedUnit.MeterPerSecond),
                        windDirection = WindDirectionValueType(it.wind.direction, WindDirectionUnit.Degree),
                    )
                }

                val dailyForecast = allWeatherDataEntity.dailyForecastEntity.items.let { items ->
                    val listMap = mutableMapOf<LocalDate, DailyForecast.DayItem>()
                    items.forEach { item ->
                        val date = LocalDate.parse(item.dateTime.value)

                        if (!listMap.containsKey(date)) {
                            listMap[date] = DailyForecast.DayItem()
                        }
                        listMap[date]?.addValue(
                            dateTime = item.dateTime,
                            weatherCondition = WeatherConditionValueType(weatherIcon = WeatherConditionValueType.icon(item.weatherCondition.value),
                                weatherCondition = item.weatherCondition.value),
                            precipitationProbability = item.precipitation.probability,
                        )
                    }

                    listMap.toList().sortedBy { it.first }.map { it.second }
                }

                val weatherInfo = WeatherInfo(
                    currentWeather = currentWeather,
                    hourlyForecast = HourlyForecast(hourlyForecast),
                    dailyForecast = DailyForecast(dailyForecast),
                    yesterdayWeather = YesterdayWeather(),
                )

                _weatherInfo.value = UiState.Success(weatherInfo)
            }.onFailure {
                _weatherInfo.value = UiState.Error(it)
            }
        }
    }
}