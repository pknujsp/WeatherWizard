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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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

private const val MIN_AUTO_SIZING_TEXT_SIZE = 12
private const val DEFAULT_AUTO_SIZING_TEXT_SIZE = 18

@Composable
fun CurrentWeatherScreen(current: CurrentWeather, yesterdayWeather: YesterdayWeather?) {

    Column {
        ConstraintLayout(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 100.dp, bottom = 12.dp, start = 12.dp, end = 12.dp)) {
            val (yesterdayTemperature, weatherIcon, condition, temperature, feelsLikeTemperature) = createRefs()

            // temperature
            Text(text = listOf(
                AStyle(
                    current.temperature.value.toInt().toString(),
                    span = SpanStyle(fontSize = 90.sp, color = textColor, letterSpacing = (-3).sp, fontWeight = FontWeight.Light),
                ),
                AStyle(current.temperature.unit.symbol,
                    span = SpanStyle(fontSize = 38.sp, color = textColor, fontWeight = FontWeight.Light)),
            ).toAnnotated(), modifier = Modifier.constrainAs(temperature) {
                baseline.linkTo(feelsLikeTemperature.baseline)
                absoluteLeft.linkTo(parent.absoluteLeft)
            }, style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle))

            // feels like temperature
            Text(text = listOf(
                AStyle("${stringResource(id = WeatherDataCategory.FEELS_LIKE_TEMPERATURE.stringId)} ",
                    span = SpanStyle(fontSize = 15.sp, color = textColor, fontWeight = FontWeight.Light)),
                AStyle(current.feelsLikeTemperature.value.toInt().toString(),
                    span = SpanStyle(fontSize = 48.sp, color = textColor, letterSpacing = (-3).sp, fontWeight = FontWeight.Light)),
                AStyle(current.feelsLikeTemperature.unit.symbol,
                    span = SpanStyle(fontSize = 22.sp, color = textColor, fontWeight = FontWeight.Light)),
            ).toAnnotated(), modifier = Modifier.constrainAs(feelsLikeTemperature) {
                absoluteRight.linkTo(parent.absoluteRight)
                bottom.linkTo(yesterdayTemperature.top)
            }, style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle))

            Box(modifier = Modifier.constrainAs(yesterdayTemperature) {
                absoluteLeft.linkTo(parent.absoluteLeft)
                bottom.linkTo(parent.bottom)
            }) {
                // yesterday temperature
                yesterdayWeather?.let {
                    Text(text = it.text(current.temperature, LocalContext.current).let { texts ->
                        listOf(
                            AStyle(texts[0], span = SpanStyle(fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Light)),
                            AStyle(" ${texts[1]} ", span = SpanStyle(fontSize = 15.sp, color = textColor, fontWeight = FontWeight.Normal)),
                            AStyle(texts[2], span = SpanStyle(fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Light)),
                        ).toAnnotated()
                    }, style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle))
                }
            }

            // weather icon
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(current.weatherIcon).crossfade(false).build(),
                contentDescription = stringResource(
                    id = io.github.pknujsp.weatherwizard.core.resource.R.string.weather_icon_description,
                ),
                modifier = Modifier
                    .size(60.dp)
                    .constrainAs(weatherIcon) {
                        absoluteLeft.linkTo(parent.absoluteLeft)
                        bottom.linkTo(temperature.top, (-8).dp)
                    },
            )

            // weather condition
            Text(
                text = stringResource(current.weatherCondition.value.stringRes),
                modifier = Modifier.constrainAs(condition) {
                    centerVerticallyTo(weatherIcon)
                    absoluteLeft.linkTo(weatherIcon.absoluteRight, 4.dp)
                },
                style = TextStyle(fontSize = 22.sp, color = textColor, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center).merge(
                    outlineTextStyle).merge(notIncludeTextPaddingStyle),
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
            .height(70.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = stringResource(id = label), style = MaterialTheme.typography.labelLarge.copy(color = Color.Black))
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomStart) {
                AutoAdjustingFontSizeText(
                    text = value,
                    modifier = Modifier.fillMaxWidth(),
                    style = TextStyle(color = Color.Black, fontWeight = FontWeight.SemiBold),
                )
            }
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


/**
 * [minFontSize] ~ [defaultFontSize] 범위 내에서, [step]만큼 fontSize를 동적으로
 * 조절하면서, [TextOverflow]가 발생하지 않도록 하는 Text Composable
 *
 * @param modifier
 * @param text
 * @param style
 * @param overflow [TextOverflow.Ellipsis]는 사용 불가(동적 크기 조절이 이루어지지 않는다)
 * @param minFontSize
 * @param defaultFontSize
 * @param step
 *
 */
@Composable
fun AutoAdjustingFontSizeText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle,
    overflow: TextOverflow = TextOverflow.Clip,
    minFontSize: Int = MIN_AUTO_SIZING_TEXT_SIZE,
    defaultFontSize: Int = DEFAULT_AUTO_SIZING_TEXT_SIZE,
    step: Int = 1
) {
    BoxWithConstraints(modifier = modifier) {
        val textOverflow = remember(overflow) { if (overflow == TextOverflow.Ellipsis) TextOverflow.Clip else overflow }
        var textStyle by remember(text) { mutableStateOf(style.copy(fontSize = defaultFontSize.sp)) }

        Text(text = text, maxLines = 1, style = textStyle, overflow = textOverflow, modifier = Modifier, onTextLayout = {
            if (textStyle.fontSize.value.toInt() > minFontSize && it.didOverflowWidth || it.didOverflowHeight) {
                val newFontSize = (textStyle.fontSize.value.toInt() - step).coerceAtLeast(minFontSize)
                textStyle = textStyle.copy(fontSize = newFontSize.sp)
            }
        })
    }
}

/*
* overflow는 무조건 clip으로 해야함
* overflow가 ellipsis로 되어있으면, width가 충분히 크더라도 ellipsis가 되어버림
*
* , onTextLayout = {
            if (it.didOverflowWidth or it.didOverflowHeight) {
                val newFontSize = (textStyle.fontSize.value.toInt() - step).coerceAtLeast(minFontSize)
                textStyle = textStyle.copy(fontSize = newFontSize.sp)
            }
        }
* */


@Composable
fun AutoText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle,
    overflow: TextOverflow = TextOverflow.Clip,
    minFontSize: Int = MIN_AUTO_SIZING_TEXT_SIZE,
    defaultFontSize: Int = DEFAULT_AUTO_SIZING_TEXT_SIZE,
    step: Int = 1
) {
    BoxWithConstraints(modifier = modifier) {
        val textMeasurer = rememberTextMeasurer()
        val textOverflow = remember(overflow) { if (overflow == TextOverflow.Ellipsis) TextOverflow.Clip else overflow }
        var textStyle by remember(text) { mutableStateOf(style.copy(fontSize = defaultFontSize.sp)) }

        LaunchedEffect(textStyle) {
            textMeasurer.measure(text, textStyle).run {
                if (textStyle.fontSize.value.toInt() > minFontSize && size.width >= constraints.maxWidth || size.height >= constraints.maxHeight) {
                    val newFontSize = (textStyle.fontSize.value.toInt() - step).coerceAtLeast(minFontSize)
                    textStyle = textStyle.copy(fontSize = newFontSize.sp)
                }
            }
        }

        Text(text = text, maxLines = 1, style = textStyle, overflow = textOverflow)
    }
}