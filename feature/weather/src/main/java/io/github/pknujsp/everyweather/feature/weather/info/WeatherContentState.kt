package io.github.pknujsp.everyweather.feature.weather.info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.SimpleHourlyForecast
import io.github.pknujsp.everyweather.feature.weather.route.NestedWeatherRoutes
import io.github.pknujsp.everyweather.feature.weather.summary.WeatherSummaryPrompt
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

sealed interface WeatherContentUiState {
    data class Error(
        val state: StatefulFeature
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
            private val dateTimeFormatter = java.time.format.DateTimeFormatter.ofPattern("MM.dd HH:mm")
        }
    }
}

@Stable
class Weather(
    val currentWeather: CurrentWeather,
    val simpleHourlyForecast: SimpleHourlyForecast,
    val detailHourlyForecast: DetailHourlyForecast,
    val simpleDailyForecast: SimpleDailyForecast,
    val detailDailyForecast: DetailDailyForecast,
    latitude: Double,
    longitude: Double,
    dateTime: ZonedDateTime
) {
    val flickrRequestParameters: FlickrRequestParameters = FlickrRequestParameters(
        weatherCondition = currentWeather.weatherCondition.value,
        latitude = latitude,
        longitude = longitude,
        refreshDateTime = dateTime,
    )
}

@Stable
private class MutableWeatherContentState(
    private val refreshFunc: () -> Unit, private val windowInsetsControllerCompat: WindowInsetsControllerCompat
) : WeatherContentState {
    override val nestedRoutes = mutableStateOf(NestedWeatherRoutes.startDestination)

    override fun navigate(nestedRoutes: NestedWeatherRoutes) {
        this.nestedRoutes.value = nestedRoutes
    }

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
fun rememberWeatherContentState(
    refresh: () -> Unit,
): WeatherContentState {
    val window = LocalContext.current.asActivity()!!.window
    val windowInsetsControllerCompat = remember(window) {
        WindowInsetsControllerCompat(window, window.decorView)
    }

    val state: WeatherContentState = remember {
        MutableWeatherContentState(refresh, windowInsetsControllerCompat)
    }

    var nestedRoutes by rememberSaveable(saver = Saver(save = { it.value.route },
        restore = { mutableStateOf(NestedWeatherRoutes.getRoute(it)) })) {
        mutableStateOf(state.nestedRoutes.value)
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
    val nestedRoutes: State<NestedWeatherRoutes>
    fun setSystemBarColor(color: SystemBarContentColor)
    fun navigate(nestedRoutes: NestedWeatherRoutes)
    fun refresh()
}