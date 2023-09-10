package io.github.pknujsp.weatherwizard.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlaceHolder(modifier: Modifier) {
    Canvas(modifier = modifier) {
        drawRoundRect(color = Color.LightGray, topLeft = Offset(0f, 0f), cornerRadius = CornerRadius(8.dp.toPx()))
    }
}