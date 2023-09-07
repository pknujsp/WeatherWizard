package io.github.pknujsp.weatherwizard.core.ui

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
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
import java.time.LocalDate
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
    scrollState: ScrollState,
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current.density

    val textLayoutResult = textMeasurer.measure(dateTimes.first().format(format), textStyle)
    val columnWidthPx = columnWidth.value * density
    val height = (textLayoutResult.size.height / density).dp + space * 2

    val dateValues = remember {
        var date = dateTimes.first().toString().run { ZonedDateTime.parse(this).toLocalDate() }
        var lastDate = LocalDate.parse(date.toString()).minusDays(5)

        val dateValueList = mutableListOf<DateValue>()
        var beginX: Float

        for (col in dateTimes.indices) {
            date = dateTimes[col].toLocalDate()

            if (date.dayOfYear != lastDate.dayOfYear || col == 0) {
                if (dateValueList.isNotEmpty()) {
                    dateValueList.last().endX = columnWidthPx * (col - 1) + (columnWidthPx / 2)
                }

                beginX = (columnWidthPx * col) + (columnWidthPx / 2)
                dateValueList.add(DateValue(beginX, date))
                lastDate = date
            }
        }
        dateValueList.last().endX = columnWidthPx * (dateTimes.size - 1) + (columnWidthPx / 2)
        dateValueList.toList()
    }
    val firstColX = remember { dateValues.first().beginX }

    Canvas(modifier = Modifier
        .width(columnWidth * dateTimes.size)
        .height(height)) {
        val y = (size.height / 2) - (textLayoutResult.size.height / 2)
        val currentX = scrollState.value
        var amountX: Float

        for (dateValue in dateValues) {
            if (currentX >= (dateValue.beginX - firstColX) && currentX < (dateValue.endX - firstColX)) {
                dateValue.lastX = currentX + firstColX
            } else if (currentX < dateValue.beginX) {
                dateValue.lastX = dateValue.beginX
            }

            drawText(textMeasurer.measure(dateValue.date.format(format), textStyle).apply { amountX = size.width / 2f }, textColor, Offset
                (dateValue.lastX - amountX, y))
        }
    }
}

private class DateValue(val beginX: Float, val date: LocalDate) {
    var endX: Float = 0f
    var lastX: Float = beginX
}