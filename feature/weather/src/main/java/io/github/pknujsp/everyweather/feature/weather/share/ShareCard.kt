package io.github.pknujsp.everyweather.feature.weather.share

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.ui.NewGraph
import io.github.pknujsp.everyweather.core.ui.route.DrawInfo
import io.github.pknujsp.everyweather.core.ui.route.SingleGraph
import io.github.pknujsp.everyweather.feature.weather.R
import io.github.pknujsp.everyweather.feature.weather.info.WeatherContentUiState
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.SimpleHourlyForecast

@Composable
fun ShareCard(
    weatherContentUiState: WeatherContentUiState.Success,
    location: String,
) {
    Box {
    }
}

@Preview(showBackground = true)
@Composable
fun Content() {
    Column {
        Text(text = "위치", fontSize = 26.sp, color = Color.Black, fontWeight = FontWeight.Bold)
        Text(text = "시간", fontSize = 13.sp, color = Color.Gray)

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AsyncImage(
                    modifier = Modifier.size(24.dp),
                    model =
                        ImageRequest.Builder(LocalContext.current)
                            .data(io.github.pknujsp.everyweather.core.resource.R.drawable.ic_weather_clear_day).build(),
                    contentDescription = null,
                )
                Text(text = "맑음", fontSize = 22.sp, color = Color.Black, fontWeight = FontWeight.Medium)
            }
            Text(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = "24도",
                fontSize = 24.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
fun HourlyForecast(simpleHourlyForecast: SimpleHourlyForecast) {
    val graphHeight = with(LocalDensity.current) { 50.dp.toPx() }
    val linePoints =
        remember(simpleHourlyForecast) {
            NewGraph(listOf(simpleHourlyForecast.items.map { it.temperatureInt })).createNewGraph(graphHeight)[0]
        }
    val itemModifier = remember { Modifier.width(SimpleHourlyForecast.itemWidth) }
    val graphDrawInfo = remember { DrawInfo() }

    Row(modifier = Modifier.fillMaxWidth()) {
        simpleHourlyForecast.items.forEachIndexed { idx, item ->
            Item(
                simpleHourlyForecast = simpleHourlyForecast,
                item = item,
                modifier = itemModifier,
                linePoint = linePoints[idx],
                drawInfo = graphDrawInfo,
            )
        }
    }
}

@Composable
private fun Item(
    simpleHourlyForecast: SimpleHourlyForecast,
    item: SimpleHourlyForecast.Item,
    modifier: Modifier,
    linePoint: NewGraph.LinePoint,
    drawInfo: DrawInfo,
) {
    // 시각, 아이콘, 강수확률, 강수량
    item.run {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = time, style = TextStyle(fontSize = 13.sp, color = Color.White))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(weatherIcon).crossfade(false).build(),
                contentDescription = null,
                modifier = Modifier.size(38.dp),
            )

            if (simpleHourlyForecast.displayPrecipitationProbability) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    AsyncImage(
                        model =
                            ImageRequest.Builder(LocalContext.current).data(SimpleHourlyForecast.Item.probabilityIcon)
                                .crossfade(false).build(),
                        contentDescription = null,
                        modifier = SimpleHourlyForecast.Item.imageModifier,
                    )
                    Text(text = precipitationProbability, style = TextStyle(fontSize = 12.sp, color = Color.White))
                }
            }

            if (simpleHourlyForecast.displayPrecipitationVolume) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    AsyncImage(
                        model =
                            ImageRequest.Builder(LocalContext.current).data(SimpleHourlyForecast.Item.rainfallIcon)
                                .crossfade(false).build(),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .alpha(if (precipitationVolume.isNotEmpty()) 1f else 0f)
                                .then(SimpleHourlyForecast.Item.imageModifier),
                    )
                    Text(
                        text = precipitationVolume,
                        style =
                            TextStyle(
                                fontSize = 12.sp,
                                color =
                                    if (simpleHourlyForecast.displayPrecipitationVolume && precipitationVolume.isNotEmpty()) {
                                        Color.White
                                    } else {
                                        Color.Transparent
                                    },
                            ),
                    )
                }
            }
            if (simpleHourlyForecast.displayRainfallVolume) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    AsyncImage(
                        model =
                            ImageRequest.Builder(LocalContext.current).data(SimpleHourlyForecast.Item.rainfallIcon)
                                .crossfade(false).build(),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .alpha(if (rainfallVolume.isNotEmpty()) 1f else 0f)
                                .then(SimpleHourlyForecast.Item.imageModifier),
                    )
                    Text(
                        text = rainfallVolume,
                        style = TextStyle(fontSize = 12.sp, color = if (rainfallVolume.isNotEmpty()) Color.White else Color.Transparent),
                    )
                }
            }

            if (simpleHourlyForecast.displaySnowfallVolume) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    AsyncImage(
                        model =
                            ImageRequest.Builder(LocalContext.current).data(SimpleHourlyForecast.Item.snowfallIcon)
                                .crossfade(false).build(),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .alpha(if (snowfallVolume.isNotEmpty()) 1f else 0f)
                                .then(SimpleHourlyForecast.Item.imageModifier),
                    )
                    Text(
                        text = snowfallVolume,
                        style = TextStyle(fontSize = 12.sp, color = if (snowfallVolume.isNotEmpty()) Color.White else Color.Transparent),
                    )
                }
            }

            SingleGraph(
                drawInfo = drawInfo,
                linePoint = linePoint,
                text = temperature,
                modifier = modifier.height(80.dp),
            )
        }
    }
}
