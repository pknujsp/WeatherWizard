package io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast.simple

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.SimpleDailyForecast
import io.github.pknujsp.weatherwizard.core.ui.DrawInfo
import io.github.pknujsp.weatherwizard.core.ui.MultiGraph
import io.github.pknujsp.weatherwizard.core.ui.NewGraph


@Composable
fun SimpleDailyForecastItem(simpleDailyForecast: SimpleDailyForecast) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val graphHeight = remember {
        with(density) {
            SimpleDailyForecast.temperatureGraphHeight.toPx()
        }
    }
    val linePoints = remember(simpleDailyForecast) {
        NewGraph(listOf(simpleDailyForecast.items.map { it.minTemperatureInt },
            simpleDailyForecast.items.map { it.maxTemperatureInt })).createNewGraph(graphHeight)
    }
    val graphDrawInfos = remember { listOf(DrawInfo(pointColor = Color.Blue), DrawInfo(pointColor = Color.Red)) }

    val lazyListState = rememberLazyListState()
    LaunchedEffect(linePoints) {
        lazyListState.scrollToItem(0, 0)
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        state = lazyListState,
    ) {
        items(count = simpleDailyForecast.items.size, key = { simpleDailyForecast.items[it].id }) { i ->
            Item(i, simpleDailyForecast, linePoints, graphDrawInfos) { conditions ->
                Toast.makeText(context, conditions.joinToString(",") { context.getString(it) }, Toast.LENGTH_SHORT).show()
            }
        }
    }
}


@Composable
private fun Item(
    index: Int,
    simpleDailyForecast: SimpleDailyForecast,
    linePoints: List<List<NewGraph.LinePoint>>,
    drawInfos: List<DrawInfo>,
    onClick: (List<Int>) -> Unit
) {
    // 날짜, 아이콘, 강수확률, 강수량
    simpleDailyForecast.items[index].run {
        Column(
            modifier = Modifier
                .width(SimpleDailyForecast.itemWidth)
                .clickable {
                    onClick(weatherConditions)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = date, style = TextStyle(fontSize = 13.sp, color = Color.White, textAlign = TextAlign.Center))
            Row(modifier = Modifier.height(38.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                weatherConditionIcons.forEach { icon ->
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(icon).crossfade(false).build(),
                        contentDescription = null,
                        modifier = Modifier.weight(1f, true))
                }
            }

            if (simpleDailyForecast.displayPrecipitationProbability) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(SimpleDailyForecast.probabilityIcon).crossfade(false)
                        .build(), contentDescription = null, modifier = Modifier
                        .size(12.dp)
                        .padding(end = 4.dp))
                    Text(text = precipitationProbabilities.joinToString("/"), style = TextStyle(fontSize = 12.sp, color = Color.White))
                }
            }

            MultiGraph(drawInfos,
                listOf(linePoints[0][index], linePoints[1][index]),
                listOf(minTemperature, maxTemperature),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(SimpleDailyForecast.itemWidth, 95.dp))
        }
    }
}