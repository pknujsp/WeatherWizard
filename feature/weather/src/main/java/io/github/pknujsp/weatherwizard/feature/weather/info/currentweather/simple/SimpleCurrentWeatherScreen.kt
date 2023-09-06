package io.github.pknujsp.weatherwizard.feature.weather.info.currentweather.simple

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.common.util.AStyle
import io.github.pknujsp.weatherwizard.core.common.util.toAnnotated
import io.github.pknujsp.weatherwizard.core.model.UiState
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataCategory
import io.github.pknujsp.weatherwizard.feature.weather.info.CardInfo
import io.github.pknujsp.weatherwizard.feature.weather.info.SimpleWeatherScreenBackground
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoViewModel


@Composable
fun CurrentWeatherScreen(weatherInfoViewModel: WeatherInfoViewModel) {
    val weatherInfo = weatherInfoViewModel.weatherInfo.collectAsStateWithLifecycle()

    weatherInfo.value.onSuccess {
        val currentWeather = (weatherInfo.value as UiState.Success).data.currentWeather

        SimpleWeatherScreenBackground(
            CardInfo(
                title = "현재 날씨",
                content =
                {
                    ConstraintLayout(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()) {
                        val (
                            weatherIcon, airQuality, temperature, weatherCondition, wind, humidity, comparedToYesterday,
                            feelsLikeTemperature,
                        ) = createRefs()

                        Text(
                            text = listOf(
                                AStyle(currentWeather.temperature.value.toInt().toString(), span = SpanStyle(fontSize = TextUnit(60f,
                                    TextUnitType.Sp))),
                                AStyle(currentWeather.temperature.unit.symbol,
                                    span = SpanStyle(fontSize = TextUnit(32f, TextUnitType.Sp))),
                            ).toAnnotated(),
                            modifier = Modifier.constrainAs(temperature) {
                                bottom.linkTo(parent.bottom)
                                absoluteLeft.linkTo(parent.absoluteLeft)
                            },
                        )

                        Image(
                            imageVector = ImageVector.vectorResource(id = currentWeather.weatherCondition.value.dayWeatherIcon),
                            contentDescription = stringResource(
                                id = io.github.pknujsp.weatherwizard.core.model.R.string.weather_icon_description,
                            ),
                            modifier = Modifier
                                .size(24.dp)
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
                            style = TextStyle(fontSize = TextUnit(20f, TextUnitType.Sp)),
                        )

                        Text(
                            text = listOf(
                                AStyle(stringResource(id = WeatherDataCategory.FEELS_LIKE_TEMPERATURE.stringId)),
                                AStyle(currentWeather.feelsLikeTemperature.value.toInt().toString(),
                                    span = SpanStyle(fontSize = TextUnit(34f, TextUnitType.Sp))),
                                AStyle(currentWeather.feelsLikeTemperature.unit.symbol,
                                    span = SpanStyle(fontSize = TextUnit(20f, TextUnitType.Sp))),
                            ).toAnnotated(),
                            modifier = Modifier.constrainAs(feelsLikeTemperature) {
                                bottom.linkTo(parent.bottom)
                                absoluteRight.linkTo(parent.absoluteRight)
                            },
                        )

                        Text(
                            text = "Good",
                            modifier = Modifier.constrainAs(airQuality) {
                                bottom.linkTo(feelsLikeTemperature.top)
                                absoluteRight.linkTo(parent.absoluteRight)
                            },
                        )
                    }
                }

            )
        )
    }
}