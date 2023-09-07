package io.github.pknujsp.weatherwizard.core.ui.weather.forecast

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity

@Composable
fun HorizontalScrollableForecast(scrollState: ScrollState, content: @Composable () -> Unit) {
    Column(Modifier
        .wrapContentHeight()
        .nestedScroll(object : NestedScrollConnection {
            override suspend fun onPreFling(available: Velocity): Velocity {
                return super.onPreFling(available)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                return super.onPostFling(consumed, available)
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                return super.onPostScroll(consumed, available, source)
            }

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return super.onPreScroll(available, source)
            }
        }, NestedScrollDispatcher())
        .horizontalScroll(scrollState)) {
        content()
    }
}