package io.github.pknujsp.weatherwizard.core.ui.weather.forecast

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HorizontalScrollableForecastScreen(content: @Composable () -> Unit) {
    val offset = remember { mutableFloatStateOf(0f) }

    Box(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .scrollable(
                orientation = Orientation.Horizontal,
                state = rememberScrollableState { delta ->
                    offset.floatValue += delta
                    delta
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}