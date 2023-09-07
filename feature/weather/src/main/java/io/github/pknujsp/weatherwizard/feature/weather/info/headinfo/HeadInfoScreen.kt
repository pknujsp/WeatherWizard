package io.github.pknujsp.weatherwizard.feature.weather.info.headinfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.common.util.AStyle
import io.github.pknujsp.weatherwizard.core.common.util.toAnnotated
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.ui.theme.outlineTextStyle
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HeadInfoScreen(weatherInfoViewModel: WeatherInfoViewModel) {
    val weatherInfo = weatherInfoViewModel.weatherInfo.collectAsStateWithLifecycle()

    weatherInfo.value.onSuccess {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 40.dp, start = 12.dp, end = 60.dp),
        ) {
            Text(
                text = listOf(
                    AStyle(
                        "대한민국\n",
                        span = SpanStyle(
                            fontSize = TextUnit(30f, TextUnitType.Sp),
                        ),
                    ),
                    AStyle("부산광역시 중구", span = SpanStyle(fontSize = TextUnit(26f, TextUnitType.Sp))),
                ).toAnnotated(),
                textAlign = TextAlign.Start,
                style = LocalTextStyle.current.merge(outlineTextStyle),
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 30.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(id = io.github.pknujsp.weatherwizard.core.common.R.drawable.round_update_24),
                    contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core.model.R.string.weather_info_head_info_update_time),
                    colorFilter = ColorFilter.tint(Color.Gray),
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = LocalDateTime.now().format(dateTimeFormatter),
                    fontSize = TextUnit(15f, TextUnitType.Sp),
                    color = Color.White,
                    style = LocalTextStyle.current.merge(outlineTextStyle),
                )
            }
        }
    }
}

private val dateTimeFormatter = DateTimeFormatter.ofPattern("M.d E HH:mm")