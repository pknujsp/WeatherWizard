package io.github.pknujsp.everyweather.core.ui.weather.item

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.pknujsp.everyweather.core.ui.time.DateTimeInfo
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object DynamicDateTimeUiCreator {
    private val formatter = DateTimeFormatter.ofPattern("M.d\nE")

    fun create(density: Density, times: List<ZonedDateTime>, itemWidth: Dp): DateTimeInfo {
        val columnWidthPx = with(density) { itemWidth.value.dp.toPx().toInt() }
        var lastDate = times.first().minusDays(5)

        val itemList = mutableListOf<DateTimeInfo.Item>()
        var beginX: Int
        var date: ZonedDateTime

        for (pos in times.indices) {
            date = times[pos]

            if (date.dayOfYear != lastDate.dayOfYear || pos == 0) {
                if (itemList.isNotEmpty()) {
                    itemList.last().endX = columnWidthPx * (pos - 1) + (columnWidthPx / 2)
                }

                beginX = (columnWidthPx * pos) + (columnWidthPx / 2)
                itemList.add(DateTimeInfo.Item(beginX, date.format(formatter)))
                lastDate = date
            }
        }

        itemList.last().endX = columnWidthPx * (times.size - 1) + (columnWidthPx / 2)
        return DateTimeInfo(itemList, itemList.first().beginX, itemWidth)
    }
}