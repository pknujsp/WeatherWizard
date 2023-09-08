package io.github.pknujsp.weatherwizard.core.common.util

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location
import java.util.Calendar
import java.util.TimeZone

class DayNightCalculator(latitude: Double, longitude: Double, timeZone: TimeZone = TimeZone.getDefault()) {

    private val calculator = SunriseSunsetCalculator(
        Location(latitude, longitude), timeZone
    )

    private val calendarCacheMap = mutableMapOf<Triple<Int, Int, Int>, Pair<Calendar, Calendar>>()

    fun calculate(currentCalendar: Calendar): DayNight {
        val (sunRise, sunSet) = calendarCacheMap.getOrPut(currentCalendar.toDay()) {
            val sunRise = calculator.getOfficialSunriseCalendarForDate(currentCalendar)
            val sunSet = calculator.getOfficialSunsetCalendarForDate(currentCalendar)
            sunRise to sunSet
        }

        return if (sunRise.before(currentCalendar) and sunSet.after(currentCalendar)) DayNight.DAY else DayNight.NIGHT
    }

    private fun Calendar.toDay(): Triple<Int, Int, Int> = Triple(this.get(Calendar.YEAR), this.get(Calendar.MONTH), this.get(Calendar.DATE))

    enum class DayNight {
        DAY, NIGHT
    }
}