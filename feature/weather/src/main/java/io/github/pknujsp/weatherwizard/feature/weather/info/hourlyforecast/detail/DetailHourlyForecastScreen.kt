package io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.detail

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
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
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.DetailHourlyForecast
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailHourlyForecastScreen(hourlyForecast: DetailHourlyForecast, popBackStack: () -> Unit) {
    val popBackStackState by rememberUpdatedState(popBackStack)
    val coroutineScope = rememberCoroutineScope()
    var showDialogWithListIndex by remember { mutableStateOf(false to -1) }

    BackHandler {
        popBackStackState()
    }



    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {
        TitleTextWithNavigation(title = stringResource(io.github.pknujsp.weatherwizard.core.resource.R.string.hourly_forecast)) {
            popBackStackState()
        }
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            hourlyForecast.items.forEach { pair ->
                stickyHeader(key = pair.first) {
                    Box(contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .background(Color.LightGray.copy(alpha = 0.8f), AppShapes.small)
                            .padding(horizontal = 16.dp, vertical = 2.dp)) {
                        Text(
                            text = pair.first,
                            style = TextStyle(fontSize = 14.sp, color = Color.White),
                        )
                    }
                }
                itemsIndexed(pair.second, key = { _, item -> item.id }) { index, item ->
                    Item(item = item,
                        displayPrecipitationProbability = hourlyForecast.displayPrecipitationProbability,
                        displayPrecipitationVolume = hourlyForecast.displayPrecipitationVolume,
                        displaySnowfallVolume = hourlyForecast.displaySnowfallVolume,
                        displayRainfallVolume = hourlyForecast.displayRainfallVolume) {
                        coroutineScope.launch {
                            showDialogWithListIndex = true to index
                        }
                    }
                }

            }
        }
    }

    if (showDialogWithListIndex.first) {
        AlertDialog(onDismissRequest = { showDialogWithListIndex = false to -1 }) {
            
        }
    }
}

@Composable
private fun Item(
    item: DetailHourlyForecast.Item,
    displayPrecipitationProbability: Boolean,
    displayPrecipitationVolume: Boolean,
    displaySnowfallVolume: Boolean,
    displayRainfallVolume: Boolean,
    onClick: () -> Unit
) {
    item.run {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clickable {
                    onClick()
                }) {
            Text(text = time,
                style = TextStyle(fontSize = 16.sp, color = Color.Gray),
                modifier = Modifier
                    .weight(0.1f, true)
                    .padding(start = 16.dp))

            Row(modifier = Modifier
                .weight(0.3f, true)
                .padding(end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(model = ImageRequest.Builder(context = LocalContext.current).data(weatherIcon).build(),
                    modifier = Modifier.size(44.dp),
                    contentDescription = null)
                Text(text = temperature, style = TextStyle(fontSize = 17.sp, color = Color.Black))
            }

            Row(modifier = Modifier.weight(0.3f, true),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically) {
                if (displayPrecipitationProbability) {
                    AsyncImage(model = ImageRequest.Builder(context = LocalContext.current)
                        .data(io.github.pknujsp.weatherwizard.core.resource.R.drawable.ic_umbrella).build(),
                        modifier = Modifier.size(14.dp),
                        contentDescription = null)
                    Text(text = precipitationProbability, style = TextStyle(fontSize = 13.sp, color = Color.Black))
                }
            }

            Column(modifier = Modifier.weight(0.3f, true),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start) {
                if (displayPrecipitationVolume) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(model = ImageRequest.Builder(context = LocalContext.current)
                            .data(io.github.pknujsp.weatherwizard.core.resource.R.drawable.raindrop).build(),
                            modifier = Modifier.size(16.dp),
                            contentDescription = null)
                        Text(text = item.precipitationVolume, style = TextStyle(fontSize = 14.sp, color = Color.Black))
                    }
                }
                if (displayRainfallVolume) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(model = ImageRequest.Builder(context = LocalContext.current)
                            .data(io.github.pknujsp.weatherwizard.core.resource.R.drawable.raindrop).build(),
                            modifier = Modifier.size(14.dp),
                            contentDescription = null)
                        Text(text = item.rainfallVolume, style = TextStyle(fontSize = 13.sp, color = Color.Black))
                    }
                }
                if (displaySnowfallVolume) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(model = ImageRequest.Builder(context = LocalContext.current)
                            .data(io.github.pknujsp.weatherwizard.core.resource.R.drawable.snowparticle).build(),
                            modifier = Modifier.size(16.dp),
                            contentDescription = null)
                        Text(text = item.snowfallVolume, style = TextStyle(fontSize = 13.sp, color = Color.Black))
                    }
                }
            }
        }
    }

}