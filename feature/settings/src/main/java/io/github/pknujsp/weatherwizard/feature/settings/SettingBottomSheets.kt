package io.github.pknujsp.weatherwizard.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataUnit
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithoutNavigation
import io.github.pknujsp.weatherwizard.core.ui.dialog.BottomSheet


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ValuesBottomSheet(
    title: String, units: Array<out WeatherDataUnit>, selectedUnit: WeatherDataUnit, onClick: (WeatherDataUnit?) ->
    Unit
) {
    BottomSheet(
        onDismissRequest = {
            onClick(null)
        },
    ) {
        Column(modifier = Modifier
            .padding(vertical = 16.dp)
        ) {
            TitleTextWithoutNavigation(title = title)
            units.forEach { unit ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .clickable {
                        onClick(unit)
                    }
                    .fillMaxWidth()) {
                    Text(
                        text = unit.symbol,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    )
                    RadioButton(selected = selectedUnit == unit, onClick = {
                        onClick(unit)
                    }, modifier = Modifier.padding(end = 12.dp))
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WeatherDataProviderBottomSheet(currentProvider: WeatherDataProvider, onClick: (WeatherDataProvider?) -> Unit) {
    BottomSheet(
        consumedNavigationBar = true,
        onDismissRequest = {
            onClick(null)
        },
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
        ) {
            TitleTextWithoutNavigation(title = stringResource(id = R.string.title_weather_data_provider))
            WeatherDataProvider.enums.forEach { weatherDataProvider ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .clickable {
                        onClick(weatherDataProvider)
                    }
                    .fillMaxWidth()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(weatherDataProvider.logo).crossfade(false)
                            .build(),
                        contentDescription = stringResource(id = R.string.title_weather_data_provider),
                        modifier = Modifier
                            .size(34.dp)
                            .padding(start = 12.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(id = weatherDataProvider.name),
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    RadioButton(selected = currentProvider == weatherDataProvider, onClick = {
                        onClick(weatherDataProvider)
                    },
                        modifier = Modifier.padding(end = 12.dp))
                }
            }

        }
    }
}