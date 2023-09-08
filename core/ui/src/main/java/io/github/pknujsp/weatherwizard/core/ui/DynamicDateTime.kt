package io.github.pknujsp.weatherwizard.core.ui

import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


private val format = DateTimeFormatter.ofPattern("M.d\nE")
private val textSize = 12.sp
private val textColor = Color.White
private val space = 2.dp
private val textStyle = TextStyle(fontSize = textSize, textAlign = androidx.compose.ui.text.style.TextAlign.Center)

@Composable
fun DynamicDateTime(
    dateTimes: List<ZonedDateTime>,
    columnWidth: Dp,
    scrollPosition: () -> Int,
) {
    val density = LocalDensity.current.density
    val textMeasurer = rememberTextMeasurer()

    val textLayoutResult = remember { textMeasurer.measure(dateTimes.first().format(format), textStyle) }
    val columnWidthPx = remember {
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, columnWidth.value, Resources.getSystem()
            .displayMetrics).toInt()
    }
    val height = remember { (textLayoutResult.size.height / density).dp + space * 2 }

    val dateValues: List<DateValue> = remember {
        var date = dateTimes.first().toLocalDate()
        var lastDate = date.minusDays(5)

        val dateValueList = mutableListOf<DateValue>()
        var beginX: Int

        for (pos in dateTimes.indices) {
            date = dateTimes[pos].toLocalDate()

            if (date.dayOfYear != lastDate.dayOfYear || pos == 0) {
                if (dateValueList.isNotEmpty())
                    dateValueList.last().endX = columnWidthPx * (pos - 1) + (columnWidthPx / 2)

                beginX = (columnWidthPx * pos) + (columnWidthPx / 2)
                dateValueList.add(DateValue(beginX, date.format(format)))
                lastDate = date
            }
        }
        dateValueList.last().endX = columnWidthPx * (dateTimes.size - 1) + (columnWidthPx / 2)
        dateValueList
    }
    val firstItemX = remember { dateValues.first().x }

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(height)) {
        val y = (size.height / 2) - (textLayoutResult.size.height / 2)
        val leftOnBoxInRow = scrollPosition()
        val rightOnBoxInRow = leftOnBoxInRow + size.width
        var amountX: Int

        for (dateValue in dateValues) {
            if (dateValue.x >= firstItemX + leftOnBoxInRow) {
                dateValue.lastX = dateValue.x - leftOnBoxInRow
            } else if (dateValue.endX < leftOnBoxInRow + firstItemX) {
                dateValue.lastX = dateValue.endX - leftOnBoxInRow
            }

            drawText(textMeasurer.measure(dateValue.date, textStyle).apply { amountX = size.width / 2 }, textColor, Offset
                ((dateValue.lastX - amountX).toFloat(), y))
        }
    }
}

private class DateValue(val x: Int, val date: String) {
    var endX: Int = 0
    var lastX: Int = x
}