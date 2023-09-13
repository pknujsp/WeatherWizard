package io.github.pknujsp.weatherwizard.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlaceHolder(modifier: Modifier, contentAlignment: Alignment = Alignment.TopStart, content: @Composable (BoxScope.() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .then(modifier)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray),
    ) {
        content?.invoke(this)
    }
}