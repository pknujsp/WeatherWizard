package io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DailyForecast
import io.github.pknujsp.weatherwizard.core.ui.DrawInfo
import io.github.pknujsp.weatherwizard.core.ui.MultiGraph
import io.github.pknujsp.weatherwizard.core.ui.NewGraph


@Composable
fun SimpleDailyForecastItem(dailyForecast: DailyForecast) {
    val context = LocalContext.current

    val graphHeight = remember {
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            DailyForecast.temperatureGraphHeight.value,
            Resources.getSystem().displayMetrics)
    }
    val linePoints = remember {
        NewGraph(listOf(dailyForecast.items.map { it.minTemperatureInt }, dailyForecast.items.map { it.maxTemperatureInt })).createNewGraph(
            graphHeight)
    }
    val graphDrawInfos = remember { listOf(DrawInfo(pointColor = Color.Blue), DrawInfo(pointColor = Color.Red)) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        state = rememberLazyListState(),
    ) {
        items(count = dailyForecast.items.size,
            key = { dailyForecast.items[it].id }) { i ->
            Item(i, dailyForecast, linePoints, graphDrawInfos) { conditions ->
                Toast.makeText(context, conditions.joinToString(",") { context.getString(it) }, Toast.LENGTH_SHORT).show()
            }
        }
    }
}


@Composable
private fun Item(
    index: Int,
    dailyForecast: DailyForecast,
    linePoints: List<List<NewGraph.LinePoint>>,
    drawInfos: List<DrawInfo>,
    onClick: (List<Int>) -> Unit
) {
    // 날짜, 아이콘, 강수확률, 강수량
    dailyForecast.items[index].run {
        Column(
            modifier = Modifier
                .width(DailyForecast.itemWidth)
                .clickable {
                    onClick(weatherConditions)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = date, style = TextStyle(fontSize = 13.sp, color = Color.White, textAlign = TextAlign.Center))
            Row(modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 6.dp)
                .fillMaxWidth()
                .height(42.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                weatherConditionIcons.forEachIndexed { idx, icon ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(icon).crossfade(false).build(),
                        contentDescription = null,
                        modifier = Modifier.weight(1f, true)
                    )
                }
            }

            if(dailyForecast.displayPrecipitationProbability){
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(DailyForecast.probabilityIcon).crossfade(false).build(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(12.dp)
                            .padding(end = 4.dp))
                    Text(text = precipitationProbabilities.joinToString("/"),
                        style = TextStyle(fontSize = 12.sp, color = Color.White))
                }
            }

            MultiGraph(drawInfos,
                listOf(linePoints[0][index], linePoints[1][index]),
                listOf(minTemperature, maxTemperature),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .size(DailyForecast.itemWidth, 95.dp))
        }
    }
}