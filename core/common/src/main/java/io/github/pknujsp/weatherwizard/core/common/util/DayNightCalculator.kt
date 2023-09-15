package io.github.pknujsp.weatherwizard.core.common.util

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location
import java.time.ZonedDateTime
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

    fun getSunRiseTime(currentCalendar: Calendar): Calendar {
        return calculator.getOfficialSunriseCalendarForDate(currentCalendar)
    }

    fun getSunSetTime(currentCalendar: Calendar): Calendar {
        return calculator.getOfficialSunsetCalendarForDate(currentCalendar)
    }

    fun getSunSetRiseTimes(currentCalendar: Calendar): List<Pair<SunSetRise, ZonedDateTime>> {
        return calculator.let { calculator ->
            val curr = calculate(currentCalendar)

            val prevs = if (curr == DayNight.NIGHT) SunSetRise.SUN_SET to calculator.getOfficialSunsetCalendarForDate(currentCalendar)
                .toZonedDateTime()
            else
                SunSetRise.SUN_RISE to calculator.getOfficialSunriseCalendarForDate(currentCalendar).toZonedDateTime()

            val next =
                if (curr == DayNight.NIGHT) SunSetRise.SUN_RISE to calculator.getOfficialSunriseCalendarForDate(currentCalendar.let {
                    (it.clone() as Calendar).apply { add(Calendar.DATE, 1) }
                }).toZonedDateTime() else
                    SunSetRise.SUN_SET to calculator
                        .getOfficialSunsetCalendarForDate(currentCalendar).toZonedDateTime()

            val third = currentCalendar.let {
                val n = (it.clone() as Calendar).apply { add(Calendar.DATE, 1) }
                if (curr == DayNight.NIGHT) SunSetRise.SUN_SET to calculator.getOfficialSunsetCalendarForDate(n).toZonedDateTime() else
                    SunSetRise.SUN_RISE to calculator.getOfficialSunsetCalendarForDate(n).toZonedDateTime()
            }

            listOf(prevs, next, third)
        }
    }

    private fun Calendar.toDay(): Triple<Int, Int, Int> = Triple(this.get(Calendar.YEAR), this.get(Calendar.MONTH), this.get(Calendar.DATE))

    enum class DayNight {
        DAY, NIGHT
    }
}