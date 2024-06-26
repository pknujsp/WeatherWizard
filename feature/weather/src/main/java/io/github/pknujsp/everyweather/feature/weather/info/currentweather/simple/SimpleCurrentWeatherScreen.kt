package io.github.pknujsp.everyweather.feature.weather.info.currentweather.simple

import androidx.annotation.StringRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.everyweather.core.common.util.AStyle
import io.github.pknujsp.everyweather.core.common.util.toAnnotated
import io.github.pknujsp.everyweather.core.model.weather.common.AirQualityValueType
import io.github.pknujsp.everyweather.core.model.weather.common.WeatherDataCategory
import io.github.pknujsp.everyweather.core.resource.R
import io.github.pknujsp.everyweather.core.ui.theme.notIncludeTextPaddingStyle
import io.github.pknujsp.everyweather.core.ui.theme.outlineTextStyle
import io.github.pknujsp.everyweather.feature.weather.info.currentweather.model.CurrentWeather

private const val MIN_AUTO_SIZING_TEXT_SIZE = 12
private const val DEFAULT_AUTO_SIZING_TEXT_SIZE = 18

private val textColor = Color.White

@Composable
fun CurrentWeatherScreen(
    current: CurrentWeather,
    airQualityValueType: () -> AirQualityValueType?,
) {
    Column {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp, start = 12.dp, end = 12.dp),
        ) {
            val (yesterdayTemp, feelsLikeTemp, icon, condition, temp) = createRefs()

            // temperature
            Text(
                text = listOf(
                    AStyle(
                        current.temperature.value.toInt().toString(),
                        span = SpanStyle(fontSize = 80.sp, color = textColor, letterSpacing = (-5).sp, fontWeight = FontWeight.Light),
                    ),
                    AStyle(
                        current.temperature.unit.symbol,
                        span = SpanStyle(fontSize = 38.sp, color = textColor, fontWeight = FontWeight.Light),
                    ),
                ).toAnnotated(),
                modifier = Modifier.constrainAs(temp) {
                    absoluteRight.linkTo(parent.absoluteRight)
                    bottom.linkTo(feelsLikeTemp.top)
                },
                style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle),
            )

            // feels like temperature
            Text(
                text = listOf(
                    AStyle(
                        "${stringResource(id = WeatherDataCategory.FEELS_LIKE_TEMPERATURE.stringId)} ",
                        span = SpanStyle(fontSize = 16.sp, color = textColor, fontWeight = FontWeight.Light),
                    ),
                    AStyle(
                        current.feelsLikeTemperature.value.toInt().toString(),
                        span = SpanStyle(fontSize = 28.sp, color = textColor, letterSpacing = (-3).sp, fontWeight = FontWeight.Light),
                    ),
                    AStyle(
                        current.feelsLikeTemperature.unit.symbol,
                        span = SpanStyle(fontSize = 18.sp, color = textColor, fontWeight = FontWeight.Light),
                    ),
                ).toAnnotated(),
                modifier = Modifier.constrainAs(feelsLikeTemp) {
                    absoluteRight.linkTo(parent.absoluteRight)
                    bottom.linkTo(parent.bottom)
                },
                style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle),
            )

            // yesterday temperature
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .constrainAs(yesterdayTemp) {
                        absoluteLeft.linkTo(parent.absoluteLeft)
                        bottom.linkTo(parent.bottom)
                    },
            ) {
                if (current.yesterdayTemperature != null) {
                    Text(
                        text = current.text(current.temperature, LocalContext.current).let { texts ->
                            listOf(
                                AStyle(texts[0], span = SpanStyle(fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Light)),
                                AStyle(
                                    " ${texts[1]} ",
                                    span = SpanStyle(fontSize = 15.sp, color = textColor, fontWeight = FontWeight.Normal),
                                ),
                                AStyle(texts[2], span = SpanStyle(fontSize = 14.sp, color = textColor, fontWeight = FontWeight.Light)),
                            ).toAnnotated()
                        },
                        style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle),
                    )
                }
            }

            val infiniteTransition = rememberInfiniteTransition(label = "")
            val rotationAngle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 8f,
                animationSpec = infiniteRepeatable(animation = tween(4000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
                label = "",
            )

            // weather icon
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(current.weatherIcon).crossfade(false).build(),
                contentDescription = stringResource(
                    id = io.github.pknujsp.everyweather.core.resource.R.string.weather_icon_description,
                ),
                modifier = Modifier
                    .rotate(rotationAngle)
                    .size(90.dp)
                    .constrainAs(icon) {
                        absoluteLeft.linkTo(parent.absoluteLeft)
                        bottom.linkTo(condition.top, 4.dp)
                    },
            )

            // weather condition
            Text(
                text = stringResource(current.weatherCondition.value.stringRes),
                modifier = Modifier.constrainAs(condition) {
                    absoluteLeft.linkTo(parent.absoluteLeft)
                    bottom.linkTo(yesterdayTemp.top)
                },
                style = TextStyle(fontSize = 25.sp, color = textColor, fontWeight = FontWeight.Medium, letterSpacing = (-1).sp).merge(
                    outlineTextStyle,
                ).merge(notIncludeTextPaddingStyle),
            )
        }

        val items: List<@Composable () -> Unit> = listOf({
            FeatureItem(WeatherDataCategory.WIND_SPEED.stringId, current.windSpeed.strength(LocalContext.current))
        }, { FeatureItem(WeatherDataCategory.WIND_DIRECTION.stringId, stringResource(id = current.windDirection.compassStringRes)) }, {
            FeatureItem(
                WeatherDataCategory.AIR_QUALITY_INDEX.stringId,
                stringResource(id = airQualityValueType()?.airQualityDescription?.descriptionStringId ?: R.string.no_data),
            )
        })

        NonlazyGrid(horizontalItemCount = 3, totalItemCount = items.size) { index ->
            items[index]()
        }
    }
}

@Composable
fun FeatureItem(
    @StringRes label: Int,
    value: String,
) {
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
    content: @Composable (Int) -> Unit,
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
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
    step: Int = 1,
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
    step: Int = 1,
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