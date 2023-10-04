package io.github.pknujsp.weatherwizard.core.ui

import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.SimpleHourlyForecast

val textSize = 12.sp
val textColor = Color.White
val space = 2.dp

@Composable
fun DynamicDateTime(
    dateTimeInfo: SimpleHourlyForecast.DateTimeInfo, lazyListState: LazyListState
) {
    DateTime(dateTimeInfo, lazyListState.firstVisibleItemScrollOffset, lazyListState.firstVisibleItemIndex)
}

@Composable
fun DynamicDateTime(
    dateTimeInfo: SimpleHourlyForecast.DateTimeInfo,
    offset: Int,
    index: Int
) {
    DateTime(dateTimeInfo, offset, index)
}

@Composable
private fun DateTime(
    dateTimeInfo: SimpleHourlyForecast.DateTimeInfo,
    offset: Int,
    index: Int
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = remember {
        TextStyle(fontSize = textSize, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
    val textLayoutResult = remember {
        textMeasurer.measure(dateTimeInfo.items.first().date, textStyle)
    }
    val columnWidthPx = remember {
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SimpleHourlyForecast.itemWidth.value, Resources.getSystem().displayMetrics)
            .toInt()
    }
    val height = remember { (textLayoutResult.size.height / Resources.getSystem().displayMetrics.density).dp + (space * 2) }

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(height)) {
        println("Recompositioning DynamicDateTime")

        val leftOnBoxInRow = index * columnWidthPx + offset
        val y = (size.height / 2) - (textLayoutResult.size.height / 2)
        val rightOnBoxInRow = leftOnBoxInRow + size.width
        var amountX: Int
        val firstItemX = dateTimeInfo.firstItemX

        for (dateValue in dateTimeInfo.items) {
            if (dateValue.beginX > rightOnBoxInRow || dateValue.endX < leftOnBoxInRow - columnWidthPx) continue

            dateValue.apply {
                displayX = if (leftOnBoxInRow + firstItemX in beginX..<endX) {
                    firstItemX
                } else if (endX <= leftOnBoxInRow + firstItemX) {
                    endX - leftOnBoxInRow
                } else {
                    beginX - leftOnBoxInRow
                }
            }

            drawText(textMeasurer.measure(dateValue.date, textStyle).apply { amountX = size.width / 2 },
                textColor,
                Offset((dateValue.displayX - amountX).toFloat(), y))
        }
    }
}