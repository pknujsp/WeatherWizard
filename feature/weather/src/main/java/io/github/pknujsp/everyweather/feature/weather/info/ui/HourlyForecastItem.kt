package io.github.pknujsp.everyweather.feature.weather.info.ui

import android.widget.Toast
import androidx.annotation.DrawableRes
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.ui.NewGraph
import io.github.pknujsp.everyweather.core.ui.route.DrawInfo
import io.github.pknujsp.everyweather.core.ui.route.SingleGraph
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.HourlyForecast

private val iconRowSpacing = 4.dp
private val iconSize = 16.dp
private val temperatureGraphHeight: Dp = 60.dp


internal object HourlyForecastItemParams {
    val itemWidth: Dp = 54.dp
}

@Composable
fun HourlyForecastItem(
    hourlyForecast: HourlyForecast,
    lazyListState: LazyListState,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val graphHeight = with(LocalDensity.current) { temperatureGraphHeight.toPx() }
    val linePoints = remember(hourlyForecast) {
        NewGraph(listOf(hourlyForecast.items.map { it.temperatureInt })).createNewGraph(graphHeight)[0]
    }
    val itemModifier = remember { Modifier.width(HourlyForecastItemParams.itemWidth) }
    val graphDrawInfo = remember { DrawInfo(density = density) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        state = lazyListState,
    ) {
        items(count = hourlyForecast.items.size, key = { hourlyForecast.items[it].id }) { i ->
            Item(i, hourlyForecast, itemModifier, linePoints[i], graphDrawInfo) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
private fun Item(
    index: Int,
    hourlyForecast: HourlyForecast,
    modifier: Modifier,
    linePoint: NewGraph.LinePoint,
    drawInfo: DrawInfo,
    onClick: (String) -> Unit,
) {
    // 시각, 아이콘, 강수확률, 강수량
    hourlyForecast.items[index].run {
        val weatherConditionText = stringResource(id = weatherCondition)
        Column(
            modifier = modifier.clickable { onClick(weatherConditionText) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = time, style = TextStyle(fontSize = 13.sp, color = Color.White))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(weatherIcon).crossfade(false).build(),
                contentDescription = weatherConditionText,
                modifier = Modifier.size(38.dp),
            )

            if (hourlyForecast.displayPrecipitationProbability) {
                SmallIconItem(
                    icon = io.github.pknujsp.everyweather.core.resource.R.drawable.ic_umbrella,
                    text = precipitationProbability,
                )
            }
            if (hourlyForecast.displayPrecipitationVolume) {
                SmallIconItem(
                    icon = io.github.pknujsp.everyweather.core.resource.R.drawable.ic_raindrop,
                    text = precipitationVolume,
                )
            }
            if (hourlyForecast.displayRainfallVolume) {
                SmallIconItem(
                    icon = io.github.pknujsp.everyweather.core.resource.R.drawable.ic_raindrop,
                    text = rainfallVolume,
                )
            }
            if (hourlyForecast.displaySnowfallVolume) {
                SmallIconItem(
                    icon = io.github.pknujsp.everyweather.core.resource.R.drawable.ic_snow_particle,
                    text = snowfallVolume,
                )
            }
            SingleGraph(
                drawInfo = drawInfo,
                linePoint = linePoint,
                text = temperature,
                modifier = modifier.height(90.dp),
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
            model = ImageRequest.Builder(LocalContext.current).data(icon).crossfade(false).build(),
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