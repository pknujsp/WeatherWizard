package io.github.pknujsp.weatherwizard.feature.weather.info

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.model.flickr.FlickrRequestParameters
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeather
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DetailDailyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.SimpleDailyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.DetailHourlyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.SimpleHourlyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeather
import io.github.pknujsp.weatherwizard.core.ui.lottie.asActivity
import io.github.pknujsp.weatherwizard.feature.weather.route.NestedWeatherRoutes
import java.time.ZonedDateTime


sealed interface WeatherContentUiState {
    data class Error(val message: FailedReason) : WeatherContentUiState

    class Success(
        val args: RequestWeatherArguments, val weather: Weather, val lastUpdatedDateTime: ZonedDateTime
    ) : WeatherContentUiState {

        val dateTime: String = lastUpdatedDateTime.format(dateTimeFormatter)

        private companion object {
            private const val DATETIME_FORMAT = "MM.dd HH:mm"
            private val dateTimeFormatter = java.time.format.DateTimeFormatter.ofPattern(DATETIME_FORMAT)
        }
    }
}

class Weather(
    val currentWeather: CurrentWeather,
    val simpleHourlyForecast: SimpleHourlyForecast,
    val detailHourlyForecast: DetailHourlyForecast,
    val simpleDailyForecast: SimpleDailyForecast,
    val detailDailyForecast: DetailDailyForecast,
    val yesterdayWeather: YesterdayWeather?,
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

class WeatherMainState @OptIn(ExperimentalMaterial3Api::class) constructor(
    val scrollState: ScrollState,
    val scrollBehavior: TopAppBarScrollBehavior,
    private val windowInsetsController: WindowInsetsControllerCompat
) {
    val nestedRoutes = mutableStateOf(NestedWeatherRoutes.startDestination)

    init {
        updateWindowInset(true)
    }


    fun navigate(nestedRoutes: NestedWeatherRoutes) {
        Log.d("WeatherMainState", "navigate: $nestedRoutes")
        this.nestedRoutes.value = nestedRoutes
        updateWindowInset(nestedRoutes !is NestedWeatherRoutes.Main)
    }


    fun updateWindowInset(isAppearanceLight: Boolean) {
        windowInsetsController.run {
            isAppearanceLightStatusBars = isAppearanceLight
            isAppearanceLightNavigationBars = isAppearanceLight
        }
    }

    suspend fun expandAppBar() {
        scrollState.scrollTo(0)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberWeatherMainState(
    scrollState: ScrollState = rememberScrollState(),
    density: Density = LocalDensity.current,
): WeatherMainState {
    val window = LocalContext.current.asActivity()!!.window
    val initialHeightOffsetLimit = remember {
        with(density) { -48.dp.toPx() }
    }
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state = rememberTopAppBarState(
        initialHeightOffsetLimit = initialHeightOffsetLimit,
    ))
    val state = remember {
        WeatherMainState(scrollState, scrollBehavior, WindowInsetsControllerCompat(window, window.decorView))
    }
    var nestedRoutes by rememberSaveable(saver = Saver(save = { it.value.route },
        restore = { mutableStateOf(NestedWeatherRoutes.getRoute(it)) })) {
        state.nestedRoutes
    }

    LaunchedEffect(Unit) {
        val heightOffsetLimit = -scrollBehavior.state.heightOffsetLimit
        val collapsedHeightOffset = scrollBehavior.state.heightOffsetLimit

        snapshotFlow {
            scrollState.value
        }.collect { y ->
            if (y <= heightOffsetLimit) {
                scrollBehavior.state.heightOffset = -y.toFloat()
            } else if (scrollBehavior.state.heightOffset != collapsedHeightOffset) {
                scrollBehavior.state.heightOffset = collapsedHeightOffset
            }
        }
    }

    return state
}