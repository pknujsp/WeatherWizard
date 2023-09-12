package io.github.pknujsp.weatherwizard.feature.airquality

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


internal enum class BarSize(val width: Dp, val height: Dp, val horizontalPadding: Dp) {
    SMALL(20.dp, 60.dp, 4.dp),
    MEDIUM(32.dp, 80.dp, 8.dp),
}

internal enum class TextStyle(val fontSize: TextUnit, val color: Color) {
    SMALL(12.sp, Color.White),
    MEDIUM(14.sp, Color.White),
}

internal sealed interface BarGraphTheme {
    val barSize: BarSize
    val textStyle: TextStyle

    data object Small : BarGraphTheme {
        override val barSize: BarSize = BarSize.SMALL
        override val textStyle: TextStyle = TextStyle.SMALL
    }

    data object Medium : BarGraphTheme {
        override val barSize: BarSize = BarSize.MEDIUM
        override val textStyle: TextStyle = TextStyle.MEDIUM
    }

}