package io.github.pknujsp.weatherwizard.feature.weather.info.hourlyforecast.simple

import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.pknujsp.weatherwizard.core.model.onError
import io.github.pknujsp.weatherwizard.core.model.onLoading
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.ui.DynamicDateTime
import io.github.pknujsp.weatherwizard.core.ui.weather.item.HourlyForecastItem
import io.github.pknujsp.weatherwizard.feature.weather.info.CardInfo
import io.github.pknujsp.weatherwizard.feature.weather.info.SimpleWeatherScreenBackground
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoViewModel
import kotlinx.coroutines.launch
import java.time.ZonedDateTime


private val columnWidth = 54.dp

@Composable
fun HourlyForecastScreen(weatherInfoViewModel: WeatherInfoViewModel) {
    val weatherInfo = weatherInfoViewModel.weatherInfo.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    var scrollPosition by remember { mutableIntStateOf(0) }
    val columnWidthPx = remember {
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, columnWidth.value, Resources.getSystem()
            .displayMetrics).toInt()
    }

    LaunchedEffect(Unit) {
        scope.launch {
            snapshotFlow {
                (lazyListState.firstVisibleItemIndex * columnWidthPx) + lazyListState.firstVisibleItemScrollOffset
            }.collect {
                scrollPosition = it
            }
        }
    }

    weatherInfo.value.onLoading { }.onError { }.onSuccess {
        SimpleWeatherScreenBackground(CardInfo(title = "시간별 예보",
            buttons = listOf(
                "비교" to { },
                "자세히" to { },
            ),
            content = {
                DynamicDateTime(dateTimes = it.hourlyForecast.items.map { ZonedDateTime.parse(it.dateTime.value) },
                    columnWidth = columnWidth) { scrollPosition }
                HourlyForecastItem(hourlyForecast = it.hourlyForecast, lazyListState, columnWidth)
            }))
    }
}