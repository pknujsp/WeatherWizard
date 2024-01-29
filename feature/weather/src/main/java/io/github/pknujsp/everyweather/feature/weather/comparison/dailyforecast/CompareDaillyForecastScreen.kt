package io.github.pknujsp.everyweather.feature.weather.comparison.dailyforecast

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.model.onLoading
import io.github.pknujsp.everyweather.core.model.onSuccess
import io.github.pknujsp.everyweather.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherConditionCategory
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model.CompareDailyForecast
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.TitleTextWithNavigation
import io.github.pknujsp.everyweather.core.ui.lottie.CancellableLoadingScreen
import io.github.pknujsp.everyweather.feature.weather.comparison.common.CommonForecastItemsScreen
import io.github.pknujsp.everyweather.feature.weather.comparison.common.CompareForecastCard
import io.github.pknujsp.everyweather.feature.weather.comparison.hourlyforecast.WeatherDataProviderInfo

@Composable
fun CompareDailyForecastScreen(
    args: RequestWeatherArguments, viewModel: CompareDailyForecastViewModel = hiltViewModel(), popBackStack: () -> Unit
) {
    BackHandler {
        popBackStack()
    }
    LaunchedEffect(args) {
        viewModel.load(args)
    }
    val forecast by viewModel.dailyForecast.collectAsStateWithLifecycle()

    Column(modifier = Modifier
        .fillMaxSize()
        .systemBarsPadding()) {
        TitleTextWithNavigation(title = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.title_comparison_daily_forecast)) {
            popBackStack()
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            forecast.onLoading {
                CancellableLoadingScreen(stringResource(id = R.string.loading_daily_forecast_data)) {
                    popBackStack()
                }
            }.onSuccess {
                CompareForecastCard.CompareCardSurface {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)) {
                        Content(it)
                    }
                }

                CommonForecastItemsScreen(it.commons)
            }
        }
    }
}

@Composable
fun Content(compareDailyForecastInfo: CompareDailyForecastInfo) {
    val itemModifier = Modifier.width(CompareDailyForecastInfo.itemWidth)
    val context = LocalContext.current
    val weatherDataProviderInfoHeight = 36.dp
    val weatherDataProviderInfoHeightPx = with(LocalDensity.current) {
        weatherDataProviderInfoHeight.toPx().toInt()
    }
    val dateTextHeight = 36.dp
    val dateTextHeightPx = with(LocalDensity.current) {
        dateTextHeight.toPx().toInt()
    }

    Layout(
        content = {
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .wrapContentHeight(),
                state = rememberLazyListState(),
            ) {
                items(count = compareDailyForecastInfo.items.size, key = { it }) { i ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(weatherDataProviderInfoHeight)) {
                        Text(text = compareDailyForecastInfo.dates[i],
                            style = TextStyle(fontSize = 13.sp, color = Color.White, textAlign = TextAlign.Center),
                            modifier = Modifier.height(dateTextHeight))
                        compareDailyForecastInfo.items[i].forEach { item ->
                            Item(item, itemModifier) { conditions ->
                                Toast.makeText(context,
                                    conditions.joinToString(", ") { context.getString(it.stringRes) },
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            compareDailyForecastInfo.weatherDataProviders.forEach {
                WeatherDataProviderInfo(it, weatherDataProviderInfoHeight)
            }
        },
        modifier = Modifier.fillMaxWidth(),
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val providersCount = compareDailyForecastInfo.weatherDataProviders.size
        val height = placeables[0].height

        val forecastRowHeight = (height - dateTextHeightPx - (weatherDataProviderInfoHeightPx * providersCount)) / providersCount

        layout(constraints.maxWidth, height) {
            placeables.first().run {
                placeRelative(0, 0)
            }
            placeables.drop(1).forEachIndexed { index, placeable ->
                placeable.placeRelative(0, dateTextHeightPx + ((weatherDataProviderInfoHeightPx + forecastRowHeight) * (index)))
            }
        }
    }

}


@Composable
private fun Item(
    item: CompareDailyForecast.Item, modifier: Modifier, onClick: (List<WeatherConditionCategory>) -> Unit
) {
    // 날짜, 아이콘, 강수확률, 강수량
    item.run {
        Column(
            modifier = modifier.clickable {
                onClick(weatherConditions)
            },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 6.dp)
                .fillMaxWidth()
                .height(42.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                weatherConditions.forEach { icon ->
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(icon.dayWeatherIcon).crossfade(false).build(),
                        contentDescription = null,
                        modifier = Modifier.weight(1f, true))
                }
            }

            Text(text = "${minTemperature}/${maxTemperature}", style = TextStyle(fontSize = 15.sp, color = Color.White))
        }
    }
}