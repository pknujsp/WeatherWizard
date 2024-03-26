package io.github.pknujsp.everyweather.feature.weather.info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat
import io.github.pknujsp.everyweather.core.common.StatefulFeature
import io.github.pknujsp.everyweather.core.common.asActivity
import io.github.pknujsp.everyweather.core.model.flickr.FlickrRequestParameters
import io.github.pknujsp.everyweather.core.model.settings.CurrentUnits
import io.github.pknujsp.everyweather.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.everyweather.core.ui.theme.SystemBarContentColor
import io.github.pknujsp.everyweather.core.ui.theme.setNavigationBarContentColor
import io.github.pknujsp.everyweather.core.ui.theme.setStatusBarContentColor
import io.github.pknujsp.everyweather.feature.weather.info.currentweather.model.CurrentWeather
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model.DetailDailyForecast
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model.SimpleDailyForecast
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.DetailHourlyForecast
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.HourlyForecast
import io.github.pknujsp.everyweather.feature.weather.summary.WeatherSummaryPrompt
import java.time.ZonedDateTime

sealed interface WeatherContentUiState {
    data class Error(
        val state: StatefulFeature,
    ) : WeatherContentUiState

    data class Success(
        val args: RequestWeatherArguments,
        val weather: Weather,
        val lastUpdatedDateTime: ZonedDateTime,
        val weatherEntities: WeatherSummaryPrompt.Model,
        val currentUnits: CurrentUnits,
    ) : WeatherContentUiState {
        val dateTime: String = lastUpdatedDateTime.format(dateTimeFormatter)

        private companion object {
            private val dateTimeFormatter = java.time.format.DateTimeFormatter.ofPattern("MM.dd EEE, a hh:mm")
        }
    }
}

@Stable
class Weather(
    val currentWeather: CurrentWeather,
    val hourlyForecast: HourlyForecast,
    val detailHourlyForecast: DetailHourlyForecast,
    val simpleDailyForecast: SimpleDailyForecast,
    val detailDailyForecast: DetailDailyForecast,
    latitude: Double,
    longitude: Double,
    dateTime: ZonedDateTime,
) {
    val flickrRequestParameters: FlickrRequestParameters =
        FlickrRequestParameters(
            weatherCondition = currentWeather.weatherCondition.value,
            latitude = latitude,
            longitude = longitude,
            refreshDateTime = dateTime,
        )
}

@Stable
private class MutableWeatherContentState(
    private val refreshFunc: () -> Unit,
    private val windowInsetsControllerCompat: WindowInsetsControllerCompat,
) : WeatherContentState {
    override fun refresh() {
        refreshFunc()
    }

    override fun setSystemBarColor(color: SystemBarContentColor) {
        windowInsetsControllerCompat.run {
            setStatusBarContentColor(color)
            setNavigationBarContentColor(color)
        }
    }
}

@Composable
fun rememberWeatherContentState(refresh: () -> Unit): WeatherContentState {
    val window = LocalContext.current.asActivity()!!.window
    val windowInsetsControllerCompat =
        remember(window) {
            WindowInsetsControllerCompat(window, window.decorView)
        }

    val state: WeatherContentState =
        remember(refresh) {
            MutableWeatherContentState(refresh, windowInsetsControllerCompat)
        }

    DisposableEffect(Unit) {
        onDispose {
            state.setSystemBarColor(SystemBarContentColor.BLACK)
        }
    }

    return state
}

@Stable
interface WeatherContentState {
    fun setSystemBarColor(color: SystemBarContentColor)
    fun refresh()
}