package io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.simple

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastJoinToString
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.ui.NewGraph
import io.github.pknujsp.everyweather.core.ui.route.DrawInfo
import io.github.pknujsp.everyweather.core.ui.route.MultiGraph
import io.github.pknujsp.everyweather.feature.weather.info.dailyforecast.model.SimpleDailyForecast


private val itemWidth: Dp = 92.dp
private val temperatureGraphHeight: Dp = 52.dp
private val iconSize: Dp = 13.dp

@Composable
fun SimpleDailyForecastItem(simpleDailyForecast: SimpleDailyForecast) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val graphHeight = remember {
        with(density) {
            temperatureGraphHeight.toPx()
        }
    }
    val linePoints = remember(simpleDailyForecast) {
        NewGraph(
            listOf(
                simpleDailyForecast.items.map { it.minTemperatureInt },
                simpleDailyForecast.items.map { it.maxTemperatureInt },
            ),
        ).createNewGraph(graphHeight)
    }
    val graphDrawInfos = remember {
        listOf(DrawInfo(pointColor = Color.Blue, density = density), DrawInfo(pointColor = Color.Red, density = density))
    }

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
                Toast.makeText(context, conditions.fastJoinToString { context.getString(it) }, Toast.LENGTH_SHORT).show()
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
    onClick: (List<Int>) -> Unit,
) {
    // 날짜, 아이콘, 강수확률, 강수량
    simpleDailyForecast.items[index].run {
        Column(
            modifier = Modifier
                .width(itemWidth)
                .clickable {
                    onClick(weatherConditions)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        ) {
            Text(text = date, style = TextStyle(fontSize = 13.sp, color = Color.White, textAlign = TextAlign.Center))
            Row(
                modifier = Modifier.height(38.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            ) {
                weatherConditionIcons.forEachIndexed { i, icon ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(icon).memoryCachePolicy(CachePolicy.ENABLED)
                            .crossfade(false).build(),
                        contentDescription = null,
                        modifier = Modifier.weight(1f, true),
                    )
                    if (i < weatherConditionIcons.lastIndex) {
                        VerticalDivider(modifier = Modifier.padding(vertical = 6.dp))
                    }
                }
            }

            if (simpleDailyForecast.displayPrecipitationProbability) {
                SmallIconItem(icon = io.github.pknujsp.everyweather.core.resource.R.drawable.ic_umbrella,
                    text = precipitationProbabilities.joinToString("/"))
            }
            if (simpleDailyForecast.displayPrecipitationVolume) {
                SmallIconItem(icon = io.github.pknujsp.everyweather.core.resource.R.drawable.ic_raindrop, text = precipitationVolume)
            }

            MultiGraph(
                drawInfos,
                listOf(linePoints[0][index], linePoints[1][index]),
                listOf(minTemperature, maxTemperature),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(itemWidth, 95.dp),
            )
        }
    }
}

@Composable
private fun SmallIconItem(
    @DrawableRes icon: Int,
    text: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(icon).memoryCachePolicy(CachePolicy.ENABLED).crossfade(false).build(),
            contentDescription = null,
            modifier = Modifier
                .alpha(if (text.isNotEmpty()) 1f else 0f)
                .size(iconSize),
        )
        Text(
            text = text,
            style = TextStyle(fontSize = 12.sp, color = if (text.isNotEmpty()) Color.White else Color.Transparent),
        )
    }
}