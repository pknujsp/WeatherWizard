package io.github.pknujsp.weatherwizard.feature.weather.info


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.pknujsp.weatherwizard.core.domain.weather.GetAllWeatherDataUseCase
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.WeatherInfo
import io.github.pknujsp.weatherwizard.core.model.weather.common.AirQualityType
import io.github.pknujsp.weatherwizard.core.model.weather.common.AirQualityUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.AirQualityValueClass
import io.github.pknujsp.weatherwizard.core.model.weather.common.HumidityValueClass
import io.github.pknujsp.weatherwizard.core.model.weather.common.IntPercentUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.TemperatureValueClass
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherConditionValueClass
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindDirectionValueClass
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedUnit
import io.github.pknujsp.weatherwizard.core.model.weather.common.WindSpeedValueClass
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeather
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

            result.onSuccess {
                val currentWeather = it.currentWeatherEntity.run {
                    CurrentWeather(weatherCondition = WeatherConditionValueClass(weatherIcon = WeatherConditionValueClass.icon
                        (weatherCondition.value),
                        weatherCondition = weatherCondition.value), temperature = TemperatureValueClass(temperature.current, TemperatureUnit
                        .Celsius),
                        feelsLikeTemperature =
                        TemperatureValueClass(temperature.feelsLike, TemperatureUnit
                            .Celsius), humidity = HumidityValueClass(humidity, IntPercentUnit()), windSpeed = WindSpeedValueClass
                            (windEntity.speed, WindSpeedUnit.MeterPerSecond),
                        windDirection = WindDirectionValueClass(windEntity.direction, WindDirectionUnit.Degree),
                        airQuality = AirQualityValueClass(AirQualityType(0.0), AirQualityUnit.AQI)
                    )
                }

                val weatherInfo = WeatherInfo(
                    currentWeather = currentWeather,
                    hourlyForecast = HourlyForecast(),
                    dailyForecast = DailyForecast(),
                    yesterdayWeather = YesterdayWeather(),
                )

                _weatherInfo.value = UiState.Success(weatherInfo)
            }.onFailure {
                _weatherInfo.value = UiState.Error(it)
            }
        }
    }
}