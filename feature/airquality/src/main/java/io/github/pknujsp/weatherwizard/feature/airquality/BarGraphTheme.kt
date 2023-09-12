package io.github.pknujsp.weatherwizard.feature.airquality

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


internal enum class BarSize(val width: Dp, val height: Dp, val horizontalPadding: Dp) {
    SMALL(38.dp, 110.dp, 8.dp),
    MEDIUM(50.dp, 160.dp, 12.dp),
}

internal enum class IndexTextStyle(val fontSize: TextUnit, val color: Color) {
    SMALL(12.sp, Color.White),
    MEDIUM(13.sp, Color.White),
}

internal enum class DateTextStyle(val fontSize: TextUnit, val color: Color) {
    SMALL(12.sp, Color.White),
    MEDIUM(13.sp, Color.White),
}

internal sealed interface BarGraphTheme {
    val barSize: BarSize
    val indexTextStyle: IndexTextStyle
    val dateTextStyle: DateTextStyle

    data object Small : BarGraphTheme {
        override val barSize: BarSize = BarSize.SMALL
        override val indexTextStyle: IndexTextStyle = IndexTextStyle.SMALL
        override val dateTextStyle: DateTextStyle = DateTextStyle.SMALL
    }

    data object Medium : BarGraphTheme {
        override val barSize: BarSize = BarSize.MEDIUM
        override val indexTextStyle: IndexTextStyle = IndexTextStyle.MEDIUM
        override val dateTextStyle: DateTextStyle = DateTextStyle.MEDIUM
    }

}