package io.github.pknujsp.weatherwizard.feature.weather.info.dailyforecast.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import io.github.pknujsp.weatherwizard.core.common.R
import io.github.pknujsp.weatherwizard.core.common.util.AStyle
import io.github.pknujsp.weatherwizard.core.common.util.toAnnotated
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.weather.dailyforecast.DetailDailyForecast
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailDailyForecastScreen(viewModel: WeatherInfoViewModel, popBackStack: () -> Unit) {
    BackHandler {
        popBackStack()
    }
    Column {
        TitleTextWithNavigation(title = stringResource(R.string.daily_forecast)) {
            popBackStack()
        }
        val dailyForecast by viewModel.detailDailyForecast.collectAsStateWithLifecycle()

        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            dailyForecast.onSuccess {
                itemsIndexed(it.items) { i, item ->
                    Item(
                        item = item,
                        displayPrecipitationProbability = it.displayPrecipitationProbability,
                    ) {

                    }


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
                .height(90.dp)
                .clickable {
                    onClick()
                }) {
            Text(text = listOf(
                AStyle(
                    text = "${date}\n",
                    span = SpanStyle(fontSize = 14.sp, color = Color.Gray)
                ),
                AStyle(
                    text = dayOfWeek,
                    span = SpanStyle(fontSize = 15.sp, color = Color.Black)
                )
            ).toAnnotated(), modifier = Modifier
                .weight(0.2f, true)
                .padding(start = 16.dp),
                lineHeight = 20.sp)
            Row(modifier = Modifier.weight(0.4f, true),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically) {
                if (displayPrecipitationProbability) {
                    AsyncImage(model = ImageRequest.Builder(context = LocalContext.current).data(R.drawable.ic_umbrella).build(),
                        modifier = Modifier.size(14.dp),
                        contentDescription = null)
                    Text(text = precipitationProbabilities.joinToString("/"), style = TextStyle(fontSize = 13.sp, color = Color.Black))
                }
            }


            Column(modifier = Modifier
                .weight(0.4f, true)
                .padding(end = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically) {
                    weatherConditionIcons.forEach {
                        AsyncImage(model = ImageRequest.Builder(context = LocalContext.current).data(it).build(),
                            filterQuality = FilterQuality.High,
                            contentDescription = null)
                    }
                }
                Text(text = "${minTemperature}/${maxTemperature}", style = TextStyle(fontSize = 15.sp, color = Color.Black))
            }
        }
    }

}