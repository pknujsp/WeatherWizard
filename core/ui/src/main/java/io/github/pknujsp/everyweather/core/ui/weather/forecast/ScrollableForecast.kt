package io.github.pknujsp.everyweather.core.ui.weather.forecast

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HorizontalScrollableForecast(scrollState: ScrollState, content: @Composable () -> Unit) {
    Column(Modifier
        .wrapContentHeight()
        .horizontalScroll(scrollState)) {
        content()
    }
}