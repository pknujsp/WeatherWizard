package io.github.pknujsp.weatherwizard.feature.airquality

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private sealed interface Size<T> {
    val SMALL: T
    val MEDIUM: T
}

internal enum class BarSize(val width: Dp, val height: Dp, val horizontalPadding: Dp) {
    SMALL(40.dp, 100.dp, 10.dp), MEDIUM(50.dp, 160.dp, 12.dp),
}


private data object IndexTextStyle : Size<TextStyle> {
    override val SMALL: TextStyle = TextStyle(fontSize = 11.sp, color = Color.White, textAlign = TextAlign.Center)
    override val MEDIUM: TextStyle = TextStyle(fontSize = 13.sp, color = Color.White, textAlign = TextAlign.Center)
}

private data object DateTextStyle : Size<TextStyle> {
    override val SMALL: TextStyle = TextStyle(fontSize = 12.sp, color = Color.White, textAlign = TextAlign.Center)
    override val MEDIUM: TextStyle = TextStyle(fontSize = 13.sp, color = Color.White, textAlign = TextAlign.Center)
}

private data object DayTextStyle : Size<TextStyle> {
    override val SMALL: TextStyle = TextStyle(fontSize = 11.sp, color = Color.White, textAlign = TextAlign.Center)
    override val MEDIUM: TextStyle = TextStyle(fontSize = 12.sp, color = Color.White, textAlign = TextAlign.Center)

}

internal sealed interface BarGraphTheme {
    val barSize: BarSize
    val indexTextStyle: TextStyle
    val dateTextStyle: TextStyle
    val dayTextStyle: TextStyle

    data object SMALL : BarGraphTheme {
        override val barSize: BarSize = BarSize.SMALL
        override val indexTextStyle: TextStyle = IndexTextStyle.SMALL
        override val dateTextStyle: TextStyle = DateTextStyle.SMALL
        override val dayTextStyle: TextStyle = DayTextStyle.SMALL
    }

    data object MEDIUM : BarGraphTheme {
        override val barSize: BarSize = BarSize.MEDIUM
        override val indexTextStyle: TextStyle = IndexTextStyle.MEDIUM
        override val dateTextStyle: TextStyle = DateTextStyle.MEDIUM
        override val dayTextStyle: TextStyle = DayTextStyle.MEDIUM
    }

}