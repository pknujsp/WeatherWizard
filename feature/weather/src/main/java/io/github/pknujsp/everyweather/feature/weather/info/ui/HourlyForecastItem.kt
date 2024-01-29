package io.github.pknujsp.everyweather.feature.weather.info.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.ui.route.DrawInfo
import io.github.pknujsp.everyweather.core.ui.NewGraph
import io.github.pknujsp.everyweather.core.ui.route.SingleGraph
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.SimpleHourlyForecast


@Composable
fun HourlyForecastItem(
    simpleHourlyForecast: SimpleHourlyForecast, lazyListState: LazyListState
) {
    val context = LocalContext.current

    val graphHeight = with(LocalDensity.current) { SimpleHourlyForecast.temperatureGraphHeight.toPx() }
    val linePoints = remember(simpleHourlyForecast) {
        NewGraph(listOf(simpleHourlyForecast.items.map { it.temperatureInt })).createNewGraph(graphHeight)[0]
    }
    val itemModifier = remember { Modifier.width(SimpleHourlyForecast.itemWidth) }
    val graphDrawInfo = remember { DrawInfo() }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        state = lazyListState,
    ) {
        items(count = simpleHourlyForecast.items.size, key = { simpleHourlyForecast.items[it].id }) { i ->
            Item(i, simpleHourlyForecast, itemModifier, linePoints[i], graphDrawInfo) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}


@Composable
private fun Item(
    index: Int,
    simpleHourlyForecast: SimpleHourlyForecast,
    modifier: Modifier,
    linePoint: NewGraph.LinePoint,
    drawInfo: DrawInfo,
    onClick: (String) -> Unit
) {
    // 시각, 아이콘, 강수확률, 강수량
    simpleHourlyForecast.items[index].run {
        val weatherConditionText = stringResource(id = weatherCondition)
        Column(
            modifier = modifier.clickable { onClick(weatherConditionText) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 시각
            Text(text = time, style = TextStyle(fontSize = 13.sp, color = Color.White))
            // 아이콘
            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(weatherIcon).crossfade(false).build(),
                contentDescription = weatherConditionText,
                modifier = Modifier.size(38.dp))

            // 강수확률
            if (simpleHourlyForecast.displayPrecipitationProbability) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(SimpleHourlyForecast.Item.probabilityIcon)
                        .crossfade(false).build(), contentDescription = null, modifier = SimpleHourlyForecast.Item.imageModifier)
                    Text(text = precipitationProbability, style = TextStyle(fontSize = 12.sp, color = Color.White))
                }
            }

            // 강수량
            if (simpleHourlyForecast.displayPrecipitationVolume) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(SimpleHourlyForecast.Item.rainfallIcon)
                        .crossfade(false).build(),
                        contentDescription = null,
                        modifier = Modifier
                            .alpha(if (precipitationVolume.isNotEmpty()) 1f else 0f)
                            .then(SimpleHourlyForecast.Item.imageModifier))
                    Text(text = precipitationVolume,
                        style = TextStyle(fontSize = 12.sp,
                            color = if (simpleHourlyForecast.displayPrecipitationVolume && precipitationVolume.isNotEmpty()) Color.White
                            else Color.Transparent))
                }
            }
            // 강우량
            if (simpleHourlyForecast.displayRainfallVolume) {
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
            if (simpleHourlyForecast.displaySnowfallVolume) {
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

            // 기온
            SingleGraph(
                drawInfo = drawInfo,
                linePoint = linePoint,
                text = temperature,
                modifier = modifier.height(90.dp),
            )
        }
    }
}