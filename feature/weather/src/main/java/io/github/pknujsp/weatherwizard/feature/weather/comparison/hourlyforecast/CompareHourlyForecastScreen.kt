package io.github.pknujsp.weatherwizard.feature.weather.comparison.hourlyforecast

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.CompareHourlyForecast
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.SimpleHourlyForecast
import io.github.pknujsp.weatherwizard.core.ui.DynamicDateTime
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.core.ui.lottie.CancellableLoadingScreen
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes
import io.github.pknujsp.weatherwizard.feature.weather.R


@Composable
fun CompareHourlyForecastScreen(args: RequestWeatherDataArgs, popBackStack: () -> Unit) {
    BackHandler {
        popBackStack()
    }
    val viewModel: CompareHourlyForecastViewModel = hiltViewModel()
    LaunchedEffect(Unit) {
        viewModel.load(args)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
    ) {
        TitleTextWithNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.title_comparison_hourly_forecast)) {
            popBackStack()
        }
        val compareForecastCard = remember {
            CompareForecastCard()
        }
        compareForecastCard.CompareCardSurface {
            val hourlyForecast by viewModel.hourlyForecast.collectAsStateWithLifecycle()
            hourlyForecast.onLoading {
                CancellableLoadingScreen {

                }
            }.onSuccess {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 12.dp)) {
                    var onScrolling by remember { mutableStateOf(Triple(0, 0, 0)) }
                    val mainLazyListState = rememberLazyListState()

                    DynamicDateTime(it.date, onScrolling.second, onScrolling.third)
                    for ((id, forecast) in it.items.withIndex()) {
                        HourlyForecastItem(id, forecast.first, forecast.second, onScrolling) { offset, index ->
                            onScrolling = Triple(id, offset, index)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HourlyForecastItem(
    listStateId: Int,
    weatherDataProvider: WeatherDataProvider, forecast: CompareHourlyForecast, newScrollPosition:
    Triple<Int, Int, Int>,
    onScrolling: (Int, Int) -> Unit
) {
    val context = LocalContext.current
    val itemModifier = Modifier.width(CompareForecast.itemWidth)
    val lazyListState = rememberLazyListState()

    Row(horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 12.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(weatherDataProvider.logo).crossfade(false).build(),
            contentDescription = stringResource(id = R.string.weather_provider),
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(id = weatherDataProvider.name),
            fontSize = 14.sp,
            color = Color.White,
        )
    }

    val scrolling by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemScrollOffset to lazyListState.firstVisibleItemIndex
        }
    }

    LaunchedEffect(scrolling) {
        onScrolling(scrolling.first, scrolling.second)
        println("LaunchedEffect(scrolling)")
    }

    LaunchedEffect(newScrollPosition) {
        if ((newScrollPosition.first != listStateId) && (newScrollPosition.second != lazyListState.firstVisibleItemScrollOffset ||
                    newScrollPosition.third != lazyListState.firstVisibleItemIndex)) {
            lazyListState.scrollToItem(newScrollPosition.third, newScrollPosition.second)
            println("LaunchedEffect(newScrollPosition)")
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        state = lazyListState,
        flingBehavior = object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                return 0f
            }
        }) {
        items(count = forecast.items.size, key = { forecast.items[it].id }) { i ->
            Item(i, forecast, itemModifier) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}


@Composable
private fun Item(
    index: Int, forecast: CompareHourlyForecast, modifier: Modifier, onClick: (String) -> Unit
) {
    // 시각, 아이콘, 강수확률, 강수량
    forecast.items[index].run {
        val weatherConditionText = stringResource(id = weatherCondition)
        Column(
            modifier = Modifier
                .then(modifier)
                .clickable { onClick(weatherConditionText) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 시각
            Text(text = hour, style = TextStyle(fontSize = 13.sp, color = Color.White))
            // 아이콘
            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(weatherIcon).crossfade(false).build(),
                contentDescription = weatherConditionText,
                modifier = Modifier
                    .padding(4.dp)
                    .size(32.dp))

            // 강수확률
            if (forecast.displayPrecipitationProbability) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(SimpleHourlyForecast.Item.probabilityIcon)
                        .crossfade(false).build(), contentDescription = null, modifier = SimpleHourlyForecast.Item.imageModifier)
                    Text(text = precipitationProbability, style = TextStyle(fontSize = 12.sp, color = Color.White))
                }
            }

            // 강수량
            if (forecast.displayPrecipitationVolume) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(SimpleHourlyForecast.Item.rainfallIcon)
                        .crossfade(false).build(),
                        contentDescription = null,
                        modifier = Modifier
                            .alpha(if (precipitationVolume.isNotEmpty()) 1f else 0f)
                            .then(SimpleHourlyForecast.Item.imageModifier))
                    Text(text = precipitationVolume,
                        style = TextStyle(fontSize = 12.sp,
                            color = if (forecast.displayPrecipitationVolume and precipitationVolume.isNotEmpty()) Color.White else Color.Transparent))
                }
            }
            // 강우량
            if (forecast.displayRainfallVolume) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(SimpleHourlyForecast.Item.rainfallIcon)
                        .crossfade(false).build(),
                        contentDescription = null,
                        modifier = Modifier
                            .alpha(if (rainfallVolume.isNotEmpty()) 1f else 0f)
                            .then(SimpleHourlyForecast.Item.imageModifier))
                    Text(text = rainfallVolume,
                        style = TextStyle(fontSize = 12.sp, color = if (rainfallVolume.isNotEmpty()) Color.White else Color.Transparent))
                }
            }

            // 강설량
            if (forecast.displaySnowfallVolume) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(SimpleHourlyForecast.Item.snowfallIcon)
                        .crossfade(false).build(),
                        contentDescription = null,
                        modifier = Modifier
                            .alpha(if (snowfallVolume.isNotEmpty()) 1f else 0f)
                            .then(SimpleHourlyForecast.Item.imageModifier))
                    Text(text = snowfallVolume,
                        style = TextStyle(fontSize = 12.sp, color = if (snowfallVolume.isNotEmpty()) Color.White else Color.Transparent))
                }
            }

            Text(text = temperature, style = TextStyle(fontSize = 13.sp, color = Color.White))
        }
    }
}

@Stable
class CompareForecastCard {

    private val surfaceModifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 12.dp)
    private val backgroundColor = Color(107, 107, 107, 217)

    @Composable
    fun CompareCardSurface(content: @Composable () -> Unit) {
        Surface(
            modifier = surfaceModifier,
            shape = AppShapes.large,
            color = backgroundColor,
        ) {
            content()
        }
    }
}

@Composable
private fun SynchronizedScrolling(lazyListStates: List<LazyListState>) {
    LaunchedEffect(lazyListStates.toTypedArray()) {
        Log.d("SynchronizedScrolling", "SynchronizedScrolling")
    }
}