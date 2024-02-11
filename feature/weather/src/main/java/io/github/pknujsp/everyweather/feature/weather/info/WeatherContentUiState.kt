package io.github.pknujsp.everyweather.feature.weather.info

import android.util.Log
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.common.StatefulFeature
import io.github.pknujsp.everyweather.core.common.asActivity
import io.github.pknujsp.everyweather.core.data.favorite.SelectedLocationModel
import io.github.pknujsp.everyweather.core.model.flickr.FlickrRequestParameters
import io.github.pknujsp.everyweather.core.model.settings.CurrentUnits
import io.github.pknujsp.everyweather.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.everyweather.core.ui.theme.SystemBarContentColor
import io.github.pknujsp.everyweather.core.ui.theme.setNavigationBarContentColor
import io.github.pknujsp.everyweather.core.ui.theme.setStatusBarContentColor
import io.github.pknujsp.everyweather.feature.permoptimize.network.NetworkState
import io.github.pknujsp.everyweather.feature.permoptimize.network.rememberAppNetworkState
import io.github.pknujsp.everyweather.feature.permoptimize.permission.PermissionManager
import io.github.pknujsp.everyweather.feature.permoptimize.permission.rememberPermissionManager
import io.github.pknujsp.everyweather.feature.weather.info.currentweather.model.CurrentWeather
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model.DetailDailyForecast
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model.SimpleDailyForecast
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.DetailHourlyForecast
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.SimpleHourlyForecast
import io.github.pknujsp.everyweather.feature.weather.route.NestedWeatherRoutes
import io.github.pknujsp.everyweather.feature.weather.summary.WeatherSummaryPrompt
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

private val topAppBarOffsetLimit = (-24).dp

sealed interface WeatherContentUiState {
    val refresh: () -> Unit

    data class Error(
        val state: StatefulFeature, override val refresh: () -> Unit,
    ) : WeatherContentUiState

    data class Success(
        val args: RequestWeatherArguments,
        val weather: Weather,
        val lastUpdatedDateTime: ZonedDateTime,
        val weatherEntities: WeatherSummaryPrompt.Model,
        val currentUnits: CurrentUnits,
        override val refresh: () -> Unit,
    ) : WeatherContentUiState {

        val dateTime: String = lastUpdatedDateTime.format(dateTimeFormatter)

        private companion object {
            private val dateTimeFormatter = java.time.format.DateTimeFormatter.ofPattern("MM.dd HH:mm")
        }
    }

}

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

private class MutableWeatherMainState @OptIn(ExperimentalMaterial3Api::class) constructor(
    override val scrollState: ScrollState,
    override val scrollBehavior: TopAppBarScrollBehavior,
    override val networkState: NetworkState,
    private val weatherContentUiState: () -> WeatherContentUiState?,
    override val locationPermissionManager: PermissionManager,
    private val updateUiStateFunc: (StatefulFeature) -> Unit,
) : WeatherMainState {
    override val nestedRoutes = mutableStateOf(NestedWeatherRoutes.startDestination)

    override fun navigate(nestedRoutes: NestedWeatherRoutes) {
        this.nestedRoutes.value = nestedRoutes
    }

    override suspend fun expandAppBar() {
        scrollState.scrollTo(0)
    }

    override fun refresh() {
        val weatherContentUiState = weatherContentUiState()
        if (weatherContentUiState != null) {
            if (networkState.isNetworkAvailable) {
                weatherContentUiState.refresh()
            } else {
                updateUiState(FeatureType.Network)
            }
        }
    }

    override fun updateUiState(state: StatefulFeature) {
        updateUiStateFunc(state)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberWeatherMainState(
    weatherContentUiState: () -> WeatherContentUiState?,
    targetLocationType: State<SelectedLocationModel?>,
    updateUiState: (StatefulFeature) -> Unit,
    scrollState: ScrollState = rememberScrollState(),
    density: Density = LocalDensity.current,
): WeatherMainState {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state = rememberTopAppBarState(
        initialHeightOffsetLimit = with(density) { topAppBarOffsetLimit.toPx() },
    ))

    val window = LocalContext.current.asActivity()!!.window
    val windowInsetsControllerCompat = remember(window) {
        WindowInsetsControllerCompat(window, window.decorView)
    }

    val networkState = rememberAppNetworkState()
    val locationPermissionManager = rememberPermissionManager(defaultPermissionType = FeatureType.Permission.Location)

    val state: WeatherMainState = remember {
        MutableWeatherMainState(
            scrollState,
            scrollBehavior,
            networkState,
            weatherContentUiState,
            locationPermissionManager,
            updateUiState,
        )
    }

    var nestedRoutes by rememberSaveable(saver = Saver(save = { it.value.route },
        restore = { mutableStateOf(NestedWeatherRoutes.getRoute(it)) })) {
        mutableStateOf(state.nestedRoutes.value)
    }

    LaunchedEffect(Unit) {
        launch {
            snapshotFlow { networkState.isNetworkAvailable }.collect {
                if (it) {
                    state.refresh()
                }
            }
        }
        launch {
            snapshotFlow { state.nestedRoutes.value }.combine(snapshotFlow {
                weatherContentUiState()
            }) { nestedRoutes, weatherContentUiState ->
                nestedRoutes to weatherContentUiState
            }.collect { (route, weatherUiState) ->
                windowInsetsControllerCompat.run {
                    val color =
                        if ((weatherUiState == null || weatherUiState is WeatherContentUiState.Error) && (route is NestedWeatherRoutes.Main)) {
                            SystemBarContentColor.BLACK
                        } else {
                            route.systemBarContentColor
                        }

                    setStatusBarContentColor(color)
                    setNavigationBarContentColor(color)
                }
            }
        }
    }

    return state
}


@Stable
interface WeatherMainState {
    val scrollState: ScrollState
    @OptIn(ExperimentalMaterial3Api::class) val scrollBehavior: TopAppBarScrollBehavior
    val networkState: NetworkState
    val locationPermissionManager: PermissionManager

    val nestedRoutes: State<NestedWeatherRoutes>
    suspend fun expandAppBar()
    fun navigate(nestedRoutes: NestedWeatherRoutes)
    fun refresh()
    fun updateUiState(state: StatefulFeature)
}