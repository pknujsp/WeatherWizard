package io.github.pknujsp.weatherwizard.feature.weather.info.currentweather.simple

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.common.util.AStyle
import io.github.pknujsp.weatherwizard.core.common.util.toAnnotated
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataCategory
import io.github.pknujsp.weatherwizard.core.ui.theme.notIncludeTextPaddingStyle
import io.github.pknujsp.weatherwizard.core.ui.theme.outlineTextStyle
import io.github.pknujsp.weatherwizard.feature.weather.R
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoViewModel


@Composable
fun CurrentWeatherScreen(weatherInfoViewModel: WeatherInfoViewModel, backgroundImageUrl: String) {
    val currentWeather by weatherInfoViewModel.currentWeather.collectAsStateWithLifecycle()

    currentWeather.onSuccess { currentWeather ->

        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
        ) {
            AsyncImage(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                model = ImageRequest.Builder(LocalContext.current).data(backgroundImageUrl).crossfade(250).build(),
                contentDescription = stringResource(R.string.background_image),
                filterQuality = FilterQuality.High,
            )

            ConstraintLayout(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
            ) {
                val (weatherIcon, airQuality, temperature, weatherCondition, wind, humidity, comparedToYesterday,
                    feelsLikeTemperature, yesterday) = createRefs()

                val yesterdayWeather by weatherInfoViewModel.yesterdayWeather.collectAsStateWithLifecycle()
                val textColor = remember { Color.White }
                val labelTextSize = remember { 13.sp }

                Text(text = listOf(
                    AStyle(currentWeather.temperature.value.toInt().toString(),
                        span = SpanStyle(fontSize = 100.sp, color = textColor, letterSpacing = (-3).sp)),
                    AStyle(currentWeather.temperature.unit.symbol,
                        span = SpanStyle(fontSize = TextUnit(38f, TextUnitType.Sp), color = textColor)),
                ).toAnnotated(), modifier = Modifier.constrainAs(temperature) {
                    bottom.linkTo(yesterday.top)
                    absoluteLeft.linkTo(parent.absoluteLeft)
                }, style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle))

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(currentWeather.weatherIcon).crossfade(true).build(),
                    contentDescription = stringResource(
                        id = io.github.pknujsp.weatherwizard.core.model.R.string.weather_icon_description,
                    ),
                    modifier = Modifier
                        .size(42.dp)
                        .constrainAs(weatherIcon) {
                            bottom.linkTo(temperature.top)
                            absoluteLeft.linkTo(parent.absoluteLeft)
                        },
                )

                Text(
                    text = stringResource(currentWeather.weatherCondition.value.stringRes),
                    modifier = Modifier
                        .absolutePadding(left = 8.dp)
                        .constrainAs(weatherCondition) {
                            bottom.linkTo(weatherIcon.bottom)
                            top.linkTo(weatherIcon.top)
                            absoluteLeft.linkTo(weatherIcon.absoluteRight)
                        },
                    style = TextStyle(fontSize = TextUnit(23f, TextUnitType.Sp), color = textColor).merge(outlineTextStyle),
                )

                Text(text = listOf(
                    AStyle("${stringResource(id = WeatherDataCategory.FEELS_LIKE_TEMPERATURE.stringId)} ",
                        span = SpanStyle(fontSize = TextUnit(15f, TextUnitType.Sp), color = textColor)),
                    AStyle(currentWeather.feelsLikeTemperature.value.toInt().toString(),
                        span = SpanStyle(fontSize = TextUnit(48f, TextUnitType.Sp), color = textColor)),
                    AStyle(currentWeather.feelsLikeTemperature.unit.symbol,
                        span = SpanStyle(fontSize = TextUnit(22f, TextUnitType.Sp), color = textColor)),
                ).toAnnotated(), modifier = Modifier.constrainAs(feelsLikeTemperature) {
                    baseline.linkTo(temperature.baseline)
                    absoluteRight.linkTo(parent.absoluteRight)
                }, style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle))

                Text(text = listOf(
                    AStyle("${stringResource(id = WeatherDataCategory.HUMIDITY.stringId)} ",
                        span = SpanStyle(fontSize = labelTextSize, color = textColor)),
                    AStyle(currentWeather.humidity.toString(),
                        span = SpanStyle(fontSize = TextUnit(16f, TextUnitType.Sp), color = textColor)),
                ).toAnnotated(), modifier = Modifier.constrainAs(humidity) {
                    bottom.linkTo(wind.top, 4.dp)
                    absoluteRight.linkTo(parent.absoluteRight)
                }, style = outlineTextStyle)

                Text(text = listOf(
                    AStyle(
                        "${currentWeather.windDirection} ",
                        span = SpanStyle(fontSize = TextUnit(14f, TextUnitType.Sp), color = textColor),
                    ),
                    AStyle(currentWeather.windSpeed.strength(LocalContext.current),
                        span = SpanStyle(fontSize = TextUnit(16f, TextUnitType.Sp), color = textColor)),
                ).toAnnotated(), modifier = Modifier.constrainAs(wind) {
                    bottom.linkTo(airQuality.top, 4.dp)
                    absoluteRight.linkTo(parent.absoluteRight)
                }, style = outlineTextStyle)

                Text(text = listOf(
                    AStyle("${stringResource(id = WeatherDataCategory.AIR_QUALITY_INDEX.stringId)} ",
                        span = SpanStyle(fontSize = labelTextSize, color = textColor)),
                    AStyle("좋음", span = SpanStyle(fontSize = TextUnit(16f, TextUnitType.Sp), color = textColor)),
                ).toAnnotated(), modifier = Modifier.constrainAs(airQuality) {
                    bottom.linkTo(feelsLikeTemperature.top, 8.dp)
                    absoluteRight.linkTo(parent.absoluteRight)
                }, style = outlineTextStyle)

                yesterdayWeather.onSuccess {
                    Text(text = it.text(currentWeather.temperature, LocalContext.current),
                        modifier = Modifier.constrainAs(yesterday) {
                            bottom.linkTo(parent.bottom)
                            absoluteLeft.linkTo(parent.absoluteLeft)
                        },
                        style = LocalTextStyle.current.merge(outlineTextStyle).merge(notIncludeTextPaddingStyle),
                        color = textColor,
                        fontSize = 15.sp)
                }
            }
        }
    }
}