package io.github.pknujsp.weatherwizard.feature.weather.comparison.dailyforecast

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherArguments
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.core.ui.lottie.CancellableLoadingScreen
import io.github.pknujsp.weatherwizard.feature.weather.comparison.hourlyforecast.CompareForecastCard
import io.github.pknujsp.weatherwizard.feature.weather.comparison.hourlyforecast.WeatherDataProviderInfo

@Composable
fun CompareDailyForecastScreen(
    args: RequestWeatherArguments, viewModel: CompareDailyForecastViewModel = hiltViewModel(), popBackStack: () -> Unit
) {
    BackHandler {
        popBackStack()
    }
    LaunchedEffect(Unit) {
        viewModel.load(args)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
    ) {
        TitleTextWithNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.title_comparison_daily_forecast)) {
            popBackStack()
        }
        val compareForecastCard = remember {
            CompareForecastCard()
        }
        compareForecastCard.CompareCardSurface {
            val forecast by viewModel.dailyForecast.collectAsStateWithLifecycle()
            forecast.onLoading {
                CancellableLoadingScreen {

                }
            }.onSuccess {
                Column(verticalArrangement = Arrangement.Center, modifier = Modifier.padding(vertical = 16.dp)) {
                    Content(it)
                }
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
                        compareDailyForecastInfo.items[i].forEachIndexed { c, item ->
                            Item(item, itemModifier) { conditions ->
                                Toast.makeText(context, conditions.joinToString(",") { context.getString(it) }, Toast.LENGTH_SHORT).show()
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
    item: io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.CompareDailyForecast.Item,
    modifier: Modifier,
    onClick: (List<Int>) -> Unit
) {
    // 날짜, 아이콘, 강수확률, 강수량
    item.run {
        Column(
            modifier = Modifier
                .width(CompareDailyForecastInfo.itemWidth)
                .clickable {
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
                weatherConditionIcons.forEach { icon ->
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(icon).crossfade(false).build(),
                        contentDescription = null,
                        modifier = Modifier.weight(1f, true))
                }
            }

            Text(text = "${minTemperature}/${maxTemperature}", style = TextStyle(fontSize = 15.sp, color = Color.White))
        }
    }
}