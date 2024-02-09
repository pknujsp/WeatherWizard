package io.github.pknujsp.everyweather.feature.weather.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.pknujsp.everyweather.core.ui.shimmer
import io.github.pknujsp.everyweather.core.ui.theme.AppShapes


@Composable
fun TextPlaceHolder(isLoading: () -> Boolean) {
    if (isLoading()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)) {
            FakeBoxItem(modifier = Modifier.fillMaxWidth())
            FakeBoxItem(modifier = Modifier.fillMaxWidth(0.3f))
        }
    }
}

@Composable
private fun FakeBoxItem(modifier: Modifier) {
    Box(modifier = modifier
        .clip(AppShapes.large)
        .background(Color.LightGray)
        .height(16.dp)
        .shimmer())
}