package io.github.pknujsp.everyweather.core.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle


val notIncludeTextPaddingStyle = TextStyle(
    platformStyle = PlatformTextStyle(includeFontPadding = false),
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.Both
    )
)

val outlineTextStyle = TextStyle(
    drawStyle = Fill,
    shadow = Shadow(
        color = Color.Black,
        blurRadius = 2.5f,
        offset = Offset(0f,0f)
    )
)