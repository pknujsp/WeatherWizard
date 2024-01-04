package io.github.pknujsp.weatherwizard.feature.weather.info.currentweather.simple

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import io.github.pknujsp.weatherwizard.core.model.weather.current.CurrentWeather
import io.github.pknujsp.weatherwizard.core.model.weather.yesterday.YesterdayWeather
import io.github.pknujsp.weatherwizard.core.ui.textColor
import io.github.pknujsp.weatherwizard.core.ui.theme.notIncludeTextPaddingStyle
import io.github.pknujsp.weatherwizard.core.ui.theme.outlineTextStyle
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoViewModel


@Composable
fun CurrentWeatherScreen(current: CurrentWeather, yesterdayWeather: YesterdayWeather?) {

    Column {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (weatherIcon, condition, temperature, feelsLikeTemperature) = createRefs()
            Text(text = listOf(
                AStyle(
                    current.temperature.value.toInt().toString(),
                    span = SpanStyle(fontSize = 90.sp, color = textColor, letterSpacing = (-3).sp, fontWeight = FontWeight.Light),
                ),
                AStyle(current.temperature.unit.symbol,
                    span = SpanStyle(fontSize = 38.sp, color = textColor, fontWeight = FontWeight.Light)),
            ).toAnnotated(), modifier = Modifier.constrainAs(temperature) {
                bottom.linkTo(parent.bottom, 12.dp)
                absoluteLeft.linkTo(parent.absoluteLeft, 12.dp)
            }, style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle))

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(current.weatherIcon).crossfade(false).build(),
                contentDescription = stringResource(
                    id = io.github.pknujsp.weatherwizard.core.resource.R.string.weather_icon_description,
                ),
                modifier = Modifier
                    .size(94.dp)
                    .constrainAs(weatherIcon) {
                        bottom.linkTo(condition.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                filterQuality = FilterQuality.High,
            )

            Text(
                text = stringResource(current.weatherCondition.value.stringRes),
                modifier = Modifier.constrainAs(condition) {
                    bottom.linkTo(temperature.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                style = TextStyle(fontSize = TextUnit(24f, TextUnitType.Sp),
                    color = textColor,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center).merge(outlineTextStyle).merge(notIncludeTextPaddingStyle),
            )

            Text(text = listOf(
                AStyle("${stringResource(id = WeatherDataCategory.FEELS_LIKE_TEMPERATURE.stringId)} ",
                    span = SpanStyle(fontSize = 15.sp, color = textColor, fontWeight = FontWeight.Light)),
                AStyle(current.feelsLikeTemperature.value.toInt().toString(),
                    span = SpanStyle(fontSize = 48.sp, color = textColor, letterSpacing = (-3).sp, fontWeight = FontWeight.Light)),
                AStyle(current.feelsLikeTemperature.unit.symbol,
                    span = SpanStyle(fontSize = 22.sp, color = textColor, fontWeight = FontWeight.Light)),
            ).toAnnotated(), modifier = Modifier.constrainAs(feelsLikeTemperature) {
                baseline.linkTo(temperature.baseline)
                absoluteRight.linkTo(parent.absoluteRight, 12.dp)
            }, style = LocalTextStyle.current.merge(notIncludeTextPaddingStyle).merge(outlineTextStyle))
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            val textColor = remember { Color.White }

            Text(text = listOf(
                AStyle("${stringResource(id = WeatherDataCategory.HUMIDITY.stringId)} ",
                    span = SpanStyle(fontSize = 13.sp, color = textColor)),
                AStyle(current.humidity.toString(), span = SpanStyle(fontSize = 15.sp, color = textColor)),
            ).toAnnotated())

            Text(text = listOf(
                AStyle(
                    "${current.windDirection} ",
                    span = SpanStyle(fontSize = 13.sp, color = textColor),
                ),
                AStyle(current.windSpeed.strength(LocalContext.current), span = SpanStyle(fontSize = 15.sp, color = textColor)),
            ).toAnnotated())

            Text(text = listOf(
                AStyle("${stringResource(id = WeatherDataCategory.AIR_QUALITY_INDEX.stringId)} ",
                    span = SpanStyle(fontSize = 13.sp, color = textColor)),
                AStyle("좋음", span = SpanStyle(fontSize = 15.sp, color = textColor)),
            ).toAnnotated())

            yesterdayWeather?.let {
                Text(text = it.text(current.temperature, LocalContext.current), color = textColor, fontSize = 14.sp)
            }
        }
    }
    
}