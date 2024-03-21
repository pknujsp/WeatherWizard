package io.github.pknujsp.everyweather.core.ui.weather.item

import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.ui.unit.Dp
import io.github.pknujsp.everyweather.core.ui.time.DateTimeInfo
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DynamicDateTimeUiCreator(times: List<String>, private val itemWidth: Dp) {
    private val dates = times.map { ZonedDateTime.parse(it).toLocalDate() }

    private companion object {
        private val formatter = DateTimeFormatter.ofPattern("M.d\nE")
    }

    operator fun invoke(): DateTimeInfo {
        val columnWidthPx =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, itemWidth.value, Resources.getSystem().displayMetrics).toInt()

        var date = dates.first()
        var lastDate = date.minusDays(5)

        val itemList = mutableListOf<DateTimeInfo.Item>()
        var beginX: Int

        for (pos in dates.indices) {
            date = dates[pos]

            if (date.dayOfYear != lastDate.dayOfYear || pos == 0) {
                if (itemList.isNotEmpty()) itemList.last().endX = columnWidthPx * (pos - 1) + (columnWidthPx / 2)

                beginX = (columnWidthPx * pos) + (columnWidthPx / 2)
                itemList.add(DateTimeInfo.Item(beginX, date.format(formatter)))
                lastDate = date
            }
        }
        itemList.last().endX = columnWidthPx * (dates.size - 1) + (columnWidthPx / 2)
        return DateTimeInfo(itemList, itemList.first().beginX, itemWidth)
    }
}
