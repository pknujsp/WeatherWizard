package io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.common.util.AStyle
import io.github.pknujsp.weatherwizard.core.common.util.toAnnotated
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DetailDailyForecast
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailDailyForecastScreen(dailyForecast: DetailDailyForecast, popBackStack: () -> Unit) {
    BackHandler {
        popBackStack()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TitleTextWithNavigation(title = stringResource(R.string.daily_forecast)) {
            popBackStack()
        }
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            itemsIndexed(dailyForecast.items) { i, item ->
                Item(
                    item = item,
                    displayPrecipitationProbability = dailyForecast.displayPrecipitationProbability,
                ) {

                }
            }
        }
    }
}

@Composable
private fun Item(
    item: DetailDailyForecast.Item, displayPrecipitationProbability: Boolean, onClick: () -> Unit
) {
    item.run {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clickable {
                    onClick()
                }) {
            Text(text = listOf(AStyle(text = "${date}\n", span = SpanStyle(fontSize = 14.sp, color = Color.Gray)),
                AStyle(text = dayOfWeek, span = SpanStyle(fontSize = 16.sp, color = Color.Black))).toAnnotated(),
                modifier = Modifier
                    .weight(0.2f, true)
                    .padding(start = 16.dp),
                lineHeight = 20.sp)

            Row(modifier = Modifier.weight(0.4f, true),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically) {
                weatherConditionIcons.forEachIndexed { i, it ->
                    AsyncImage(model = ImageRequest.Builder(context = LocalContext.current).data(it).build(),
                        filterQuality = FilterQuality.High,
                        modifier = Modifier.size(52.dp),
                        contentDescription = null)
                    if (i < weatherConditionIcons.lastIndex) {
                        Box(modifier = Modifier
                            .width(1.dp)
                            .height(16.dp)
                            .background(Color.LightGray))
                    }
                }
            }

            Column(modifier = Modifier
                .weight(0.4f, true)
                .padding(end = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (displayPrecipitationProbability) {
                        AsyncImage(model = ImageRequest.Builder(context = LocalContext.current).data(R.drawable.ic_umbrella).build(),
                            modifier = Modifier.size(14.dp),
                            contentDescription = null)
                        Text(text = precipitationProbabilities.joinToString("/"), style = TextStyle(fontSize = 14.sp, color = Color.Black))
                    }
                }
                Text(text = "$minTemperature / $maxTemperature", style = TextStyle(fontSize = 16.sp, color = Color.Black))
            }
        }
    }

}