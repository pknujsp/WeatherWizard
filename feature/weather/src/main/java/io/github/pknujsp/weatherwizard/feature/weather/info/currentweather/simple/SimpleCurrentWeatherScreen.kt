package io.github.pknujsp.weatherwizard.feature.weather.info.currentweather.simple

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.common.util.AStyle
import io.github.pknujsp.weatherwizard.core.common.util.toAnnotated
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataCategory
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeather
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeather
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.textColor
import io.github.pknujsp.weatherwizard.core.ui.theme.notIncludeTextPaddingStyle
import io.github.pknujsp.weatherwizard.core.ui.theme.outlineTextStyle
import kotlinx.serialization.json.JsonNull.content

private const val featureItemValueTextSize = 21

@Composable
fun CurrentWeatherScreen(current: CurrentWeather, yesterdayWeather: YesterdayWeather?) {

    Column {
        ConstraintLayout(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 100.dp, bottom = 12.dp)) {
            val (yesterdayTemperature, weatherIcon, condition, temperature, feelsLikeTemperature) = createRefs()
            Text(text = listOf(
                AStyle(
                    current.temperature.value.toInt().toString(),
                    span = SpanStyle(fontSize = 90.sp, color = textColor, letterSpacing = (-3).sp, fontWeight = FontWeight.Light),
                ),
                AStyle(current.temperature.unit.symbol,
                    span = SpanStyle(fontSize = 38.sp, color = textColor, fontWeight = FontWeight.Light)),
            ).toAnnotated(), modifier = Modifier.constrainAs(temperature) {
                baseline.linkTo(feelsLikeTemperature.baseline)
                absoluteRight.linkTo(feelsLikeTemperature.absoluteLeft, 8.dp)
            }, style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle))

            Text(text = listOf(
                AStyle("${stringResource(id = WeatherDataCategory.FEELS_LIKE_TEMPERATURE.stringId)} ",
                    span = SpanStyle(fontSize = 15.sp, color = textColor, fontWeight = FontWeight.Light)),
                AStyle(current.feelsLikeTemperature.value.toInt().toString(),
                    span = SpanStyle(fontSize = 48.sp, color = textColor, letterSpacing = (-3).sp, fontWeight = FontWeight.Light)),
                AStyle(current.feelsLikeTemperature.unit.symbol,
                    span = SpanStyle(fontSize = 22.sp, color = textColor, fontWeight = FontWeight.Light)),
            ).toAnnotated(), modifier = Modifier.constrainAs(feelsLikeTemperature) {
                absoluteRight.linkTo(parent.absoluteRight, 12.dp)
                bottom.linkTo(yesterdayTemperature.top)
            }, style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle))

            yesterdayWeather?.let {
                Text(text = it.text(current.temperature, LocalContext.current),
                    color = textColor,
                    fontSize = 14.sp,
                    modifier = Modifier.constrainAs(yesterdayTemperature) {
                        absoluteRight.linkTo(parent.absoluteRight, 12.dp)
                        bottom.linkTo(parent.bottom)
                    },
                    style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle))
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(current.weatherIcon).crossfade(false).build(),
                contentDescription = stringResource(
                    id = io.github.pknujsp.weatherwizard.core.resource.R.string.weather_icon_description,
                ),
                modifier = Modifier
                    .size(70.dp)
                    .constrainAs(weatherIcon) {
                        absoluteLeft.linkTo(parent.absoluteLeft, 12.dp)
                        bottom.linkTo(condition.top)
                    },
                filterQuality = FilterQuality.High,
            )

            Text(
                text = stringResource(current.weatherCondition.value.stringRes),
                modifier = Modifier.constrainAs(condition) {
                    centerHorizontallyTo(weatherIcon)
                    bottom.linkTo(parent.bottom)
                },
                style = TextStyle(fontSize = TextUnit(22f, TextUnitType.Sp),
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center).merge(outlineTextStyle).merge(notIncludeTextPaddingStyle),
            )
        }

        val items: List<@Composable () -> Unit> = listOf({
            FeatureItem(WeatherDataCategory.WIND_SPEED.stringId, current.windSpeed.strength(LocalContext.current))
        }, { FeatureItem(WeatherDataCategory.WIND_DIRECTION.stringId, current.windDirection.toString()) }, {
            FeatureItem(WeatherDataCategory.AIR_QUALITY_INDEX.stringId,
                stringResource(id = current.airQuality?.airQualityDescription?.descriptionStringId ?: R.string.no_data))
        })

        NonlazyGrid(horizontalItemCount = 3, totalItemCount = items.size) { index ->
            items[index]()
        }
    }
}

@Composable
fun FeatureItem(@StringRes label: Int, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.padding(12.dp)) {
            Text(text = stringResource(id = label), style = MaterialTheme.typography.labelLarge.copy(color = Color.Black))
            AutoSizingText(
                text = value, modifier = Modifier.fillMaxWidth(),
                style = TextStyle(color = Color.Black, fontWeight = FontWeight.SemiBold),
            )
        }
    }
}


@Composable
fun NonlazyGrid(
    horizontalItemCount: Int,
    totalItemCount: Int,
    modifier: Modifier = Modifier,
    itemHorizontalPadding: Dp = 12.dp,
    itemVerticalPadding: Dp = 8.dp,
    content: @Composable (Int) -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(itemVerticalPadding)) {
        val verticalLineCount = remember {
            (totalItemCount / horizontalItemCount) + if ((totalItemCount % horizontalItemCount) > 0) 1 else 0
        }
        for (verticalLineId in 0..<verticalLineCount) {
            val firstIndex = verticalLineId * horizontalItemCount

            Row(
                horizontalArrangement = Arrangement.spacedBy(itemHorizontalPadding),
            ) {
                for (id in 0..<horizontalItemCount) {
                    val index = firstIndex + id
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), contentAlignment = Alignment.Center) {
                        if (index < totalItemCount) {
                            content(index)
                        }
                    }
                }
            }
        }
    }
}

private const val SLOT_ID = "text"

@Composable
fun AutoSizingText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    minFontSize: Int = 12,
    maxFontSize: Int = featureItemValueTextSize,
    step: Int = 1
) {
    BoxWithConstraints(modifier = modifier) {
        var fontSize by remember { mutableIntStateOf(maxFontSize) }
        val constraintsWidth = constraints.maxWidth

        // Measure the text with the current font size
        Text(text = text, maxLines = 1, style = style.copy(fontSize = fontSize.sp), modifier = Modifier.onSizeChanged { textSize ->
            if (textSize.width >= constraintsWidth) {
                fontSize = (fontSize - step).coerceAtLeast(minFontSize)
            }
        }, onTextLayout = { textLayoutResult ->

        })
    }
}