package io.github.pknujsp.weatherwizard.core.ui.weather.item

import android.content.res.Resources
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.HourlyForecast
import io.github.pknujsp.weatherwizard.core.ui.DrawInfo
import io.github.pknujsp.weatherwizard.core.ui.NewGraph
import io.github.pknujsp.weatherwizard.core.ui.SingleGraph
import java.time.ZonedDateTime


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HourlyForecastItem(hourlyForecast: HourlyForecast, lazyListState: LazyListState, columnWidth: Dp) {
    val displayRainfallVolume = remember { hourlyForecast.items.any { it.rainfallVolume.value > 0 } }
    val displaySnowfallVolume = remember { hourlyForecast.items.any { it.snowfallVolume.value > 0 } }
    val displayPrecipitationVolume = remember { hourlyForecast.items.any { it.precipitationVolume.value > 0 } }
    val context = LocalContext.current

    val graphHeight = remember { TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80f, Resources.getSystem().displayMetrics) }
    val linePoints = remember {
        NewGraph(listOf(hourlyForecast.items.map { it.temperature.value.toInt() })).createNewGraph(graphHeight)[0]
    }
    val itemModifier = remember { Modifier.width(columnWidth) }
    val graphDrawInfo = remember { DrawInfo() }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        state = lazyListState,
    ) {
        itemsIndexed(hourlyForecast.items) { i, hourly ->
            Item(item = ItemData(time = ZonedDateTime.parse(hourly.dateTime.value).hour.toString(),
                icon = hourly.weatherIcon,
                temperature = hourly.temperature.toString(),
                weatherCondition = hourly.weatherCondition.value.stringRes,
                precipitationProbability = hourly.precipitationProbability.toString(),
                precipitationVolume = hourly.precipitationVolume.toStringWithoutUnit(),
                rainfallVolume = hourly.rainfallVolume.toStringWithoutUnit(),
                snowfallVolume = hourly.snowfallVolume.toStringWithoutUnit(),
                displayRainfallVolume = displayRainfallVolume,
                displaySnowfallVolume = displaySnowfallVolume,
                displayPrecipitationVolume = displayPrecipitationVolume), itemModifier, linePoints[i], graphDrawInfo) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}


@Composable
private fun Item(item: ItemData, modifier: Modifier, linePoint: NewGraph.LinePoint, drawInfo: DrawInfo, onClick: (String) -> Unit) {
    // 시각, 아이콘, 강수확률, 강수량
    item.run {
        val weatherConditionText = stringResource(id = weatherCondition)
        Column(
            modifier = Modifier
                .then(modifier)
                .clickable { onClick(weatherConditionText) },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = time, style = TextStyle(fontSize = 13.sp, color = Color.White))
            Image(imageVector = ImageVector.vectorResource(id = icon),
                contentDescription = weatherConditionText,
                modifier = Modifier.padding(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Image(imageVector = ImageVector.vectorResource(id = ItemData.probabilityIcon),
                    contentDescription = null,
                    modifier = ItemData.imageModifier)
                Text(text = precipitationProbability, style = TextStyle(fontSize = 12.sp, color = Color.White))
            }

            if (displayPrecipitationVolume and precipitationVolume.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Image(imageVector = ImageVector.vectorResource(id = ItemData.rainfallIcon),
                        contentDescription = null,
                        modifier = ItemData.imageModifier)
                    Text(text = precipitationVolume, style = TextStyle(fontSize = 12.sp, color = Color.White))
                }
            }
            if (displayRainfallVolume and rainfallVolume.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Image(imageVector = ImageVector.vectorResource(id = ItemData.rainfallIcon),
                        contentDescription = null,
                        modifier = ItemData.imageModifier)
                    Text(text = rainfallVolume, style = TextStyle(fontSize = 12.sp, color = Color.White))
                }
            }
            if (displaySnowfallVolume and snowfallVolume.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Image(imageVector = ImageVector.vectorResource(id = ItemData.snowfallIcon),
                        contentDescription = null,
                        modifier = ItemData.imageModifier)
                    Text(text = snowfallVolume, style = TextStyle(fontSize = 12.sp, color = Color.White))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            SingleGraph(
                drawInfo = drawInfo,
                linePoint = linePoint,
                text = item.temperature,
                modifier = Modifier
                    .then(modifier)
                    .height(100.dp),
                textMeasurer = rememberTextMeasurer(),
            )
        }
    }
}


private data class ItemData(
    val time: String,
    val temperature: String,
    @DrawableRes val icon: Int,
    @StringRes val weatherCondition: Int,
    val precipitationProbability: String,
    val precipitationVolume: String,
    val rainfallVolume: String,
    val snowfallVolume: String,
    val displayRainfallVolume: Boolean,
    val displaySnowfallVolume: Boolean,
    val displayPrecipitationVolume: Boolean,
) {
    companion object {
        @DrawableRes val probabilityIcon = io.github.pknujsp.weatherwizard.core.common.R.drawable.pop
        @DrawableRes val rainfallIcon = io.github.pknujsp.weatherwizard.core.common.R.drawable.raindrop
        @DrawableRes val snowfallIcon = io.github.pknujsp.weatherwizard.core.common.R.drawable.snowparticle
        val imageModifier = Modifier
            .size(12.dp)
            .padding(end = 4.dp)
    }
}