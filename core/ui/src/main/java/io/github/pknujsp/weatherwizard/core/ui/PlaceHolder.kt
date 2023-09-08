package io.github.pknujsp.weatherwizard.core.ui

import android.content.res.Resources
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize

@Composable
fun PlaceHolder(size: DpSize) {
    val color = rememberSaveable { Color.Gray }
    val cornerRadius = rememberSaveable { CornerRadius(4f * Resources.getSystem().displayMetrics.density) }

    Canvas(modifier = Modifier.size(size)) {
        drawRoundRect(color = color, topLeft = Offset(0f, 0f), cornerRadius = cornerRadius)
    }
}