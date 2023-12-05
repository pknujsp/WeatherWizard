package io.github.pknujsp.weatherwizard.feature.weather.info

import android.app.Activity
import android.content.Context
import android.view.Window
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import io.github.pknujsp.weatherwizard.core.model.ProcessState
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.flickr.FlickrRequestParameters
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeather
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DetailDailyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.SimpleDailyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.DetailHourlyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.SimpleHourlyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeather
import io.github.pknujsp.weatherwizard.feature.weather.NestedWeatherRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


@Stable
interface WeatherMainUiState {
    val isGpsEnabled: Boolean
    val processState: ProcessState
    val args: RequestWeatherArguments
    val lastUpdatedTime: String
    val weather: Weather?
    val flickrRequestParameters: FlickrRequestParameters?
}

data class Weather(
    val currentWeather: CurrentWeather,
    val simpleHourlyForecast: SimpleHourlyForecast,
    val detailHourlyForecast: DetailHourlyForecast,
    val simpleDailyForecast: SimpleDailyForecast,
    val detailDailyForecast: DetailDailyForecast,
    val yesterdayWeather: YesterdayWeather?,
)

data class WeatherMainState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val weatherMainUiState: WeatherMainUiState,
    val window: Window,
    val viewModelStoreOwner: ViewModelStoreOwner?,
    val scrollState: ScrollState,
    val scrollBehavior: TopAppBarScrollBehavior,
) {
    val nestedRoutes = mutableStateOf(NestedWeatherRoutes.startDestination)
    var backgroundImageUrl by mutableStateOf("")
    var reload by mutableIntStateOf(0)
        private set
    private val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

    fun navigate(nestedRoutes: NestedWeatherRoutes) {
        this.nestedRoutes.value = nestedRoutes
        windowInsetsController.isAppearanceLightNavigationBars = nestedRoutes !is NestedWeatherRoutes.Main
    }

    fun reload() {
        reload++
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberWeatherMainState(
    weatherMainUiState: WeatherMainUiState,
    context: Context = LocalContext.current,
    viewModelStoreOwner: ViewModelStoreOwner? = LocalViewModelStoreOwner.current,
    scrollState: ScrollState = rememberScrollState(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
): WeatherMainState {
    val state = remember(weatherMainUiState) {
        WeatherMainState(weatherMainUiState, (context as Activity).window, viewModelStoreOwner, scrollState, scrollBehavior)
    }
    var nestedRoutes by rememberSaveable(saver = Saver(save = { it.value.route },
        restore = { mutableStateOf(NestedWeatherRoutes.getRoute(it)) })) {
        state.nestedRoutes
    }
    return state
}