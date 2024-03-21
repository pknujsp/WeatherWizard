package io.github.pknujsp.everyweather.core.ui.time

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val textSize = 12.sp
private val textColor = Color.White
private val space = 2.dp

@Composable
fun DynamicDateTime(
    dateTimeInfo: DateTimeInfo,
    lazyListState: LazyListState,
) {
    DateTime(dateTimeInfo, lazyListState)
}

@Composable
private fun DateTime(
    dateTimeInfo: DateTimeInfo,
    lazyListState: LazyListState,
    density: Density = LocalDensity.current,
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle =
        remember {
            TextStyle(fontSize = textSize, textAlign = TextAlign.Center)
        }
    val textLayoutResult =
        remember(dateTimeInfo) {
            textMeasurer.measure(dateTimeInfo.items.first().date, textStyle)
        }
    val columnWidthPx =
        remember {
            with(density) {
                dateTimeInfo.itemWidth.toPx().toInt()
            }
        }
    val height by remember(textLayoutResult) {
        derivedStateOf { (textLayoutResult.size.height / density.density).dp + (space * 2) }
    }
    val leftOnBoxInRow by remember {
        derivedStateOf {
            (lazyListState.firstVisibleItemIndex * columnWidthPx) + lazyListState.firstVisibleItemScrollOffset
        }
    }

    Canvas(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(height),
    ) {
        val y = (size.height / 2) - (textLayoutResult.size.height / 2)
        val rightOnBoxInRow = leftOnBoxInRow + size.width
        var amountX: Int
        val firstItemX = dateTimeInfo.firstItemX

        for (dateValue in dateTimeInfo.items) {
            if (dateValue.beginX > rightOnBoxInRow || dateValue.endX < leftOnBoxInRow - columnWidthPx) continue

            dateValue.apply {
                displayX =
                    if (leftOnBoxInRow + firstItemX in beginX..<endX) {
                        firstItemX
                    } else if (endX <= leftOnBoxInRow + firstItemX) {
                        endX - leftOnBoxInRow
                    } else {
                        beginX - leftOnBoxInRow
                    }
            }

            drawText(
                textMeasurer.measure(dateValue.date, textStyle).apply { amountX = size.width / 2 },
                textColor,
                Offset((dateValue.displayX - amountX).toFloat(), y),
            )
        }
    }
}

class DateTimeInfo(val items: List<Item>, val firstItemX: Int, val itemWidth: Dp) {
    class Item(val beginX: Int, val date: String) {
        var endX: Int = 0
        var displayX: Int = beginX
    }
}
