package io.github.pknujsp.weatherwizard.feature.weather.info.headinfo

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.common.util.AStyle
import io.github.pknujsp.weatherwizard.core.common.util.toAnnotated
import io.github.pknujsp.weatherwizard.core.ui.theme.outlineTextStyle
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoViewModel

@Composable
fun HeadInfoScreen(weatherInfoViewModel: WeatherInfoViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 100.dp, start = 12.dp, end = 60.dp),
    ) {
        val uiState = remember { weatherInfoViewModel.uiState }

        Text(
            text = listOf(
                AStyle(
                    "${uiState.args.location.country}\n",
                    span = SpanStyle(
                        fontSize = TextUnit(26f, TextUnitType.Sp),
                    ),
                ),
                AStyle(uiState.args.location.address, span = SpanStyle(fontSize = TextUnit(28f, TextUnitType.Sp))),
            ).toAnnotated(),
            textAlign = TextAlign.Start,
            style = LocalTextStyle.current.merge(outlineTextStyle),
            fontWeight = FontWeight.Bold,
            color = Color.White,
            lineHeight = 32.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(io.github.pknujsp.weatherwizard.core.resource.R.drawable.round_update_24)
                    .crossfade(false).build(),
                contentDescription = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.weather_info_head_info_update_time),
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = uiState.lastUpdatedTime,
                fontSize = TextUnit(15f, TextUnitType.Sp),
                color = Color.White,
                style = LocalTextStyle.current.merge(outlineTextStyle),
            )
        }
    }
}