package io.github.pknujsp.weatherwizard.core.ui.weather.item

import android.content.res.Resources
import android.util.TypedValue
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecast
import io.github.pknujsp.weatherwizard.core.ui.DrawInfo
import io.github.pknujsp.weatherwizard.core.ui.NewGraph
import io.github.pknujsp.weatherwizard.core.ui.SingleGraph


@Composable
fun HourlyForecastItem(hourlyForecast: HourlyForecast, lazyListState: LazyListState) {
    val context = LocalContext.current

    val graphHeight = remember {
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            HourlyForecast.temperatureGraphHeight.value,
            Resources.getSystem().displayMetrics)
    }
    val linePoints = remember {
        NewGraph(listOf(hourlyForecast.items.map { it.temperatureInt })).createNewGraph(graphHeight)[0]
    }
    val itemModifier = remember { Modifier.width(HourlyForecast.itemWidth) }
    val graphDrawInfo = remember { DrawInfo() }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        state = lazyListState,
    ) {
        items(hourlyForecast.items.size) { i ->
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
    onClick: (String) -> Unit
) {
    // 시각, 아이콘, 강수확률, 강수량
    hourlyForecast.items[index].run {
        val weatherConditionText = stringResource(id = weatherCondition)
        Column(
            modifier = Modifier
                .then(modifier)
                .clickable { onClick(weatherConditionText) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = time, style = TextStyle(fontSize = 13.sp, color = Color.White))
            AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                .data(weatherIcon)
                .crossfade(false)
                .build(),
                contentDescription = weatherConditionText,
                modifier = Modifier.padding(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                    .data(HourlyForecast.Item.probabilityIcon)
                    .crossfade(false)
                    .build(),
                    contentDescription = null,
                    modifier = HourlyForecast.Item.imageModifier)
                Text(text = precipitationProbability, style = TextStyle(fontSize = 12.sp, color = Color.White))
            }

            if (hourlyForecast.displayPrecipitationVolume and precipitationVolume.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                        .data(HourlyForecast.Item.rainfallIcon)
                        .crossfade(false)
                        .build(),
                        contentDescription = null,
                        modifier = HourlyForecast.Item.imageModifier)
                    Text(text = precipitationVolume, style = TextStyle(fontSize = 12.sp, color = Color.White))
                }
            }
            if (hourlyForecast.displayRainfallVolume and rainfallVolume.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                        .data(HourlyForecast.Item.rainfallIcon)
                        .crossfade(false)
                        .build(),
                        contentDescription = null,
                        modifier = HourlyForecast.Item.imageModifier)
                    Text(text = rainfallVolume, style = TextStyle(fontSize = 12.sp, color = Color.White))
                }
            }
            if (hourlyForecast.displaySnowfallVolume and snowfallVolume.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                        .data(HourlyForecast.Item.snowfallIcon)
                        .crossfade(false)
                        .build(),
                        contentDescription = null,
                        modifier = HourlyForecast.Item.imageModifier)
                    Text(text = snowfallVolume, style = TextStyle(fontSize = 12.sp, color = Color.White))
                }
            }

            SingleGraph(
                drawInfo = drawInfo,
                linePoint = linePoint,
                text = temperature,
                modifier = Modifier
                    .then(modifier)
                    .height(100.dp),
            )
        }
    }
}