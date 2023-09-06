package io.github.pknujsp.weatherwizard.core.ui.weather.forecast

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HorizontalScrollableForecast(scrollState: ScrollState, content: @Composable () -> Unit) {
    Box(
        Modifier
            .wrapContentHeight()
            .horizontalScroll(scrollState),
    ) {
        Column {
            content()
        }
    }
}