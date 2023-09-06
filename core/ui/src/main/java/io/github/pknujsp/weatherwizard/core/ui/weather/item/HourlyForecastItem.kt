package io.github.pknujsp.weatherwizard.core.ui.weather.item

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecast
import java.time.ZonedDateTime


@Composable
fun HourlyForecastItem(hourlyForecast: HourlyForecast, modifier: Modifier) {
    Column(modifier = Modifier
        .then(modifier)
        .fillMaxWidth()
        .wrapContentHeight(), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top) {
        val displayRainfallVolume = hourlyForecast.items.any { it.rainfallVolume.value > 0 }
        val displaySnowfallVolume = hourlyForecast.items.any { it.snowfallVolume.value > 0 }
        val displayPrecipitationVolume = hourlyForecast.items.any { it.precipitationVolume.value > 0 }
        val itemModifier = Modifier
            .then(modifier)
            .wrapContentHeight()

        hourlyForecast.items.forEach { hourly ->
            Item(
                item = ItemData(
                    time = ZonedDateTime.parse(hourly.dateTime.value).hour.toString(),
                    icon = hourly.weatherCondition.value.dayWeatherIcon,
                    weatherCondition = hourly.weatherCondition.value.stringRes,
                    precipitationProbability = hourly.precipitationProbability.toString(),
                    precipitationVolume = hourly.precipitationVolume.toString(),
                    displayRainfallVolume = displayRainfallVolume,
                    displaySnowfallVolume = displaySnowfallVolume,
                    displayPrecipitationVolume = displayPrecipitationVolume
                ),
                itemModifier
            )
        }
    }
}


@Composable
private fun Item(item: ItemData, modifier: Modifier) {
    // 시각, 아이콘, 강수확률, 강수량
    item.run {
        val weatherConditionText = stringResource(id = weatherCondition)
        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = time, style = TextStyle(fontSize = 13.sp, color = Color.White))
            Image(imageVector = ImageVector.vectorResource(id = icon), contentDescription = weatherConditionText)

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Image(imageVector = ImageVector.vectorResource(id = ItemData.probabilityIcon), contentDescription = null,
                    modifier = ItemData.imageModifier)
                Text(text = precipitationProbability, style = TextStyle(fontSize = 12.sp, color = Color.White))
            }

            if (displayRainfallVolume) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Image(imageVector = ImageVector.vectorResource(id = ItemData.rainfallIcon), contentDescription = null,
                        modifier = ItemData.imageModifier)
                    Text(text = precipitationVolume, style = TextStyle(fontSize = 12.sp, color = Color.White))
                }
            }
            if (displaySnowfallVolume) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Image(imageVector = ImageVector.vectorResource(id = ItemData.snowfallIcon), contentDescription = null,
                        modifier = ItemData.imageModifier)
                    Text(text = precipitationVolume, style = TextStyle(fontSize = 12.sp, color = Color.White))
                }
            }
        }
    }
}

private data class ItemData(
    val time: String,
    @DrawableRes val icon: Int,
    @StringRes val weatherCondition: Int,
    val precipitationProbability: String,
    val precipitationVolume: String,
    val displayRainfallVolume: Boolean,
    val displaySnowfallVolume: Boolean,
    val displayPrecipitationVolume: Boolean,
) {
    companion object {
        @DrawableRes val probabilityIcon = io.github.pknujsp.weatherwizard.core.common.R.drawable.pop
        @DrawableRes val rainfallIcon = io.github.pknujsp.weatherwizard.core.common.R.drawable.raindrop
        @DrawableRes val snowfallIcon = io.github.pknujsp.weatherwizard.core.common.R.drawable.snowparticle
        val imageModifier = Modifier.size(12.dp)
    }
}