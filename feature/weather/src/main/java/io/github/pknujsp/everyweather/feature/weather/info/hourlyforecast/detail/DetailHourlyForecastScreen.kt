package io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.ui.dialog.BottomSheet
import io.github.pknujsp.everyweather.core.ui.dialog.BottomSheetType
import io.github.pknujsp.everyweather.core.ui.dialog.ContentWithTitle
import io.github.pknujsp.everyweather.core.ui.theme.AppShapes
import io.github.pknujsp.everyweather.feature.weather.info.hourlyforecast.model.DetailHourlyForecast

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DetailHourlyForecastScreen(
    hourlyForecast: DetailHourlyForecast,
    popBackStack: () -> Unit,
) {
    BottomSheet(onDismissRequest = popBackStack, bottomSheetType = BottomSheetType.PERSISTENT) {
        ContentWithTitle(title = stringResource(id = io.github.pknujsp.everyweather.core.resource.R.string.hourly_forecast)) {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                hourlyForecast.items.forEachIndexed { headerIndex, (header, items) ->
                    stickyHeader(key = header.id) {
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier
                                .background(Color.LightGray.copy(alpha = 0.8f), AppShapes.small)
                                .padding(horizontal = 16.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = header.title,
                                style = TextStyle(fontSize = 14.sp, color = Color.White),
                            )
                        }
                    }
                    itemsIndexed(items, key = { _, item -> item.id }) { itemIndex, _ ->
                        Item(
                            forecast = hourlyForecast,
                            parentIndex = headerIndex,
                            itemIndex = itemIndex,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Item(
    forecast: DetailHourlyForecast,
    parentIndex: Int,
    itemIndex: Int,
) {
    forecast.items[parentIndex].second[itemIndex].run {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)) {
            Text(
                text = hour,
                style = TextStyle(fontSize = 14.sp, color = Color.Gray),
                modifier = Modifier
                    .weight(0.1f, true)
                    .padding(start = 16.dp),
            )

            Row(
                modifier = Modifier
                    .weight(0.35f, true)
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current).data(weatherIcon).build(),
                    modifier = Modifier.fillMaxHeight(),
                    contentDescription = null,
                )
                Text(text = temperature, style = TextStyle(fontSize = 16.sp, color = Color.Black))
            }

            Row(
                modifier = Modifier.weight(0.25f, true),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (forecast.displayPrecipitationProbability) {
                    AsyncImage(
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(io.github.pknujsp.everyweather.core.resource.R.drawable.ic_umbrella).build(),
                        modifier = Modifier.size(14.dp),
                        contentDescription = null,
                    )
                    Text(text = precipitationProbability, style = TextStyle(fontSize = 13.sp, color = Color.Black))
                }
            }

            Column(
                modifier = Modifier.weight(0.3f, true),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                if (forecast.displayPrecipitationVolume) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = ImageRequest.Builder(context = LocalContext.current)
                                .data(io.github.pknujsp.everyweather.core.resource.R.drawable.ic_raindrop).build(),
                            modifier = Modifier.size(16.dp),
                            contentDescription = null,
                        )
                        Text(text = precipitationVolume, style = TextStyle(fontSize = 13.sp, color = Color.Black))
                    }
                }
                if (forecast.displayRainfallVolume) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = ImageRequest.Builder(context = LocalContext.current)
                                .data(io.github.pknujsp.everyweather.core.resource.R.drawable.ic_raindrop).build(),
                            modifier = Modifier.size(14.dp),
                            contentDescription = null,
                        )
                        Text(text = rainfallVolume, style = TextStyle(fontSize = 13.sp, color = Color.Black))
                    }
                }
                if (forecast.displaySnowfallVolume) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = ImageRequest.Builder(context = LocalContext.current)
                                .data(io.github.pknujsp.everyweather.core.resource.R.drawable.ic_snow_particle).build(),
                            modifier = Modifier.size(16.dp),
                            contentDescription = null,
                        )
                        Text(text = snowfallVolume, style = TextStyle(fontSize = 13.sp, color = Color.Black))
                    }
                }
            }
        }
    }
}