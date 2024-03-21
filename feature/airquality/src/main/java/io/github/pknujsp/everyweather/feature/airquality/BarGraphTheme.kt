package io.github.pknujsp.everyweather.feature.airquality

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private sealed interface Size<T> {
    val small: T
    val medium: T
}

internal enum class BarSize(val width: Dp, val height: Dp, val horizontalPadding: Dp) {
    SMALL(40.dp, 85.dp, 8.dp),
    MEDIUM(50.dp, 160.dp, 12.dp),
}

private data object IndexTextStyle : Size<TextStyle> {
    override val small: TextStyle = TextStyle(fontSize = 12.sp, color = Color.White, textAlign = TextAlign.Center)
    override val medium: TextStyle = TextStyle(fontSize = 13.sp, color = Color.White, textAlign = TextAlign.Center)
}

private data object DateTextStyle : Size<TextStyle> {
    override val small: TextStyle = TextStyle(fontSize = 13.sp, color = Color.White, textAlign = TextAlign.Center)
    override val medium: TextStyle = TextStyle(fontSize = 14.sp, color = Color.White, textAlign = TextAlign.Center)
}

private data object DayTextStyle : Size<TextStyle> {
    override val small: TextStyle = TextStyle(fontSize = 12.sp, color = Color.White, textAlign = TextAlign.Center)
    override val medium: TextStyle = TextStyle(fontSize = 13.sp, color = Color.White, textAlign = TextAlign.Center)
}

internal sealed interface BarGraphTheme {
    val barSize: BarSize
    val indexTextStyle: TextStyle
    val dateTextStyle: TextStyle
    val dayTextStyle: TextStyle

    data object Small : BarGraphTheme {
        override val barSize: BarSize = BarSize.SMALL
        override val indexTextStyle: TextStyle = IndexTextStyle.small
        override val dateTextStyle: TextStyle = DateTextStyle.small
        override val dayTextStyle: TextStyle = DayTextStyle.small
    }

    data object Medium : BarGraphTheme {
        override val barSize: BarSize = BarSize.MEDIUM
        override val indexTextStyle: TextStyle = IndexTextStyle.medium
        override val dateTextStyle: TextStyle = DateTextStyle.medium
        override val dayTextStyle: TextStyle = DayTextStyle.medium
    }
}
