package io.github.pknujsp.everyweather.core.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle


val notIncludeTextPaddingStyle = TextStyle(
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

val outlineTextStyle = TextStyle(
    drawStyle = Fill,
    shadow = Shadow(
        color = Color.Black,
        blurRadius = 2.5f,
        offset = Offset(0f,0f)
    )
)