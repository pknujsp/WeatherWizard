package io.github.pknujsp.weatherwizard.feature.weather.info


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherProvider
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithoutNavigation
import io.github.pknujsp.weatherwizard.core.ui.dialog.BottomSheet
import io.github.pknujsp.weatherwizard.feature.weather.route.NestedWeatherRoutes
import io.github.pknujsp.weatherwizard.feature.weather.comparison.dailyforecast.CompareDailyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.comparison.hourlyforecast.CompareHourlyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast.detail.DetailDailyForecastScreen
import io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.detail.DetailHourlyForecastScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherInfoScreen(navController: NavController, viewModel: WeatherInfoViewModel = hiltViewModel()) {
    val mainState = rememberWeatherMainState(weatherContentUiState = viewModel.uiState)

    LaunchedEffect(mainState.reload) {
        viewModel.initialize()
    }

    when (mainState.nestedRoutes.value) {
        is NestedWeatherRoutes.Main -> {
            val contentArguments = ContentArguments(
                mainState.weatherContentUiState,
                mainState.scrollState,
                mainState.scrollBehavior,
                navigate = {
                    mainState.navigate(it)
                },
                reload = {
                    mainState.reload()
                },
                onDayChanged = { isDay ->
                    mainState.updateWindowInsetByTime(isDay)
                },
            )
            WeatherContentScreen(contentArguments, viewModel)
        }

        is NestedWeatherRoutes.DetailHourlyForecast -> {
            DetailHourlyForecastScreen(mainState.weatherContentUiState.weather!!.detailHourlyForecast) {
                mainState.navigate(NestedWeatherRoutes.Main)
            }
        }

        is NestedWeatherRoutes.DetailDailyForecast -> {
            DetailDailyForecastScreen(mainState.weatherContentUiState.weather!!.detailDailyForecast) {
                mainState.navigate(NestedWeatherRoutes.Main)
            }
        }

        is NestedWeatherRoutes.ComparisonDailyForecast -> {
            CompareDailyForecastScreen(mainState.weatherContentUiState.args) {
                mainState.navigate(NestedWeatherRoutes.Main)
            }
        }

        is NestedWeatherRoutes.ComparisonHourlyForecast -> {
            CompareHourlyForecastScreen(mainState.weatherContentUiState.args, mainState.viewModelStoreOwner!!) {
                mainState.navigate(NestedWeatherRoutes.Main)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherProviderDialog(currentProvider: WeatherProvider, onClick: (WeatherProvider?) -> Unit) {
    BottomSheet(
        onDismissRequest = {
            onClick(null)
        },
    ) {
        Column(modifier = Modifier) {
            TitleTextWithoutNavigation(title = stringResource(id = R.string.weather_provider))
            WeatherProvider.enums.forEach { weatherDataProvider ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .clickable {
                        onClick(weatherDataProvider)
                    }
                    .fillMaxWidth()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(weatherDataProvider.icon).crossfade(false).build(),
                        contentDescription = stringResource(id = R.string.weather_provider),
                        modifier = Modifier
                            .size(34.dp)
                            .padding(start = 12.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(id = weatherDataProvider.title),
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f))
                    RadioButton(selected = currentProvider == weatherDataProvider, onClick = {
                        onClick(weatherDataProvider)
                    }, modifier = Modifier.padding(end = 12.dp))
                }
            }
        }
    }
}