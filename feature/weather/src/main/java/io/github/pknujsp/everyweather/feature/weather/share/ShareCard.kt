package io.github.pknujsp.everyweather.feature.weather.share

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.ui.NewGraph
import io.github.pknujsp.everyweather.core.ui.route.DrawInfo
import io.github.pknujsp.everyweather.feature.weather.info.WeatherContentUiState
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.HourlyForecast

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
fun HourlyForecast(hourlyForecast: HourlyForecast) {
    val graphHeight = with(LocalDensity.current) { 50.dp.toPx() }
    val linePoints =
        remember(hourlyForecast) {
            NewGraph(listOf(hourlyForecast.items.map { it.temperatureInt })).createNewGraph(graphHeight)[0]
        }
    val density = LocalDensity.current
    val graphDrawInfo = remember { DrawInfo(density = density) }

    Row(modifier = Modifier.fillMaxWidth()) {
        hourlyForecast.items.forEachIndexed { idx, item ->
            Item(
                hourlyForecast = hourlyForecast,
                item = item,
                modifier = Modifier.width( 56.dp),
                linePoint = linePoints[idx],
                drawInfo = graphDrawInfo,
            )
        }
    }
}

@Composable
private fun Item(
    hourlyForecast: HourlyForecast,
    item: HourlyForecast.Item,
    modifier: Modifier,
    linePoint: NewGraph.LinePoint,
    drawInfo: DrawInfo,
) {

}