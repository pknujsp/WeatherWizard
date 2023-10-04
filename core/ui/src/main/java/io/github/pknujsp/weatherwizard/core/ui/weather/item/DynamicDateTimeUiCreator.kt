package io.github.pknujsp.weatherwizard.core.ui.weather.item

import android.content.res.Resources
import android.util.TypedValue
import androidx.compose.ui.unit.Dp
import io.github.pknujsp.weatherwizard.core.model.weather.hourlyforecast.SimpleHourlyForecast
import io.github.pknujsp.weatherwizard.core.ui.UiComponentCreator
import java.time.ZonedDateTime

class DynamicDateTimeUiCreator(dates: List<String>, private val itemWidth: Dp) : UiComponentCreator {
    private val formatter = java.time.format.DateTimeFormatter.ofPattern("M.d\nE")
    private val dates = dates.map { ZonedDateTime.parse(it).toLocalDate() }

    operator fun invoke(): SimpleHourlyForecast.Date {
        val columnWidthPx =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, itemWidth.value, Resources.getSystem().displayMetrics).toInt()

        var date = dates.first()
        var lastDate = date.minusDays(5)

        val itemList = mutableListOf<SimpleHourlyForecast.Date.Item>()
        var beginX: Int

        for (pos in dates.indices) {
            date = dates[pos]

            if (date.dayOfYear != lastDate.dayOfYear || pos == 0) {
                if (itemList.isNotEmpty()) itemList.last().endX = columnWidthPx * (pos - 1) + (columnWidthPx / 2)

                beginX = (columnWidthPx * pos) + (columnWidthPx / 2)
                itemList.add(SimpleHourlyForecast.Date.Item(beginX, date.format(formatter)))
                lastDate = date
            }
        }
        itemList.last().endX = columnWidthPx * (dates.size - 1) + (columnWidthPx / 2)
        return SimpleHourlyForecast.Date(itemList, itemList.first().beginX)
    }
}