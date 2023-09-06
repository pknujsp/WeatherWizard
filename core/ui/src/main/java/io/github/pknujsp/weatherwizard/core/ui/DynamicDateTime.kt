package io.github.pknujsp.weatherwizard.core.ui

import android.content.res.Resources
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


private val format = DateTimeFormatter.ofPattern("M.d\nE")
private val textSize = 12.sp
private val textColor = Color.White
private val space = 2.dp

@Composable
fun DynamicDateTime(modifier: Modifier = Modifier, dateTimes: List<ZonedDateTime>, xAxisWidth: Int, currentX: MutableIntState) {
    val columnWidth = xAxisWidth.dp.value
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(dateTimes.first().format(format))
    val height = (textLayoutResult.size.height / Resources.getSystem().displayMetrics.density).dp + space


    var date = dateTimes.first().toString().run { ZonedDateTime.parse(this) }
    var lastDate = ZonedDateTime.parse(date.toString())
    lastDate = lastDate.minusDays(5)

    val timeZone = date.zone

    val dateValueList = mutableListOf<DateValue>()
    val dateValues by remember { mutableStateOf(dateValueList.toList()) }
    var beginX = 0

    for (col in dateTimes.indices) {
        date = ZonedDateTime.of(dateTimes[col].toLocalDateTime(), timeZone)

        if (date.dayOfYear != lastDate.dayOfYear || col == 0) {
            if (dateValueList.size > 0) {
                dateValueList[dateValueList.size - 1].endX = (columnWidth * (col - 1) + columnWidth / 2)
            }
            beginX = (columnWidth * col + columnWidth / 2).toInt()
            dateValueList.add(DateValue(beginX, date))
            lastDate = date
        }
    }
    dateValueList[dateValueList.size - 1].endX = columnWidth * (dateTimes.size - 1) + columnWidth / 2

    val firstColX by remember { mutableIntStateOf(0) }

    Canvas(
        modifier = Modifier
            .fillMaxWidth(columnWidth * dateTimes.size)
            .height(height)
    ) {
        val y = size.height / 2
        val x = currentX.intValue

        for (dateValue in dateValues) {
            if (x >= dateValue.beginX - firstColX && x < dateValue.endX - firstColX) {
                dateValue.lastX = (x + firstColX).toFloat()
            } else if (x < dateValue.beginX) {
                dateValue.lastX = dateValue.beginX.toFloat()
            }

            drawText(textMeasurer.measure(dateValue.date.format(format), style = TextStyle(fontSize = textSize)),
                textColor, Offset(dateValue.lastX, y))
        }
    }
}

private class DateValue(val beginX: Int, val date: ZonedDateTime) {
    var endX = 0f
    var lastX = beginX.toFloat()
}