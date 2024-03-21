package io.github.pknujsp.everyweather.core.common.util

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator
import com.luckycatlabs.sunrisesunset.dto.Location
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.TimeZone

class DayNightCalculator(latitude: Double, longitude: Double, timeZone: TimeZone = TimeZone.getDefault()) {
    private val calculator = SunriseSunsetCalculator(Location(latitude, longitude), timeZone)

    private val calendarCacheMap = mutableMapOf<Triple<Int, Int, Int>, Pair<Calendar, Calendar>>()

    fun calculate(currentCalendar: Calendar): DayNight {
        val (sunRise, sunSet) =
            calendarCacheMap.getOrPut(currentCalendar.toDay()) {
                val sunRise = calculator.getOfficialSunriseCalendarForDate(currentCalendar)
                val sunSet = calculator.getOfficialSunsetCalendarForDate(currentCalendar)
                sunRise to sunSet
            }
        return if (sunRise.before(currentCalendar) and sunSet.after(currentCalendar)) DayNight.DAY else DayNight.NIGHT
    }

    private fun getSunRiseTime(currentCalendar: Calendar): Calendar {
        return calculator.getOfficialSunriseCalendarForDate(currentCalendar)
    }

    private fun getSunSetTime(currentCalendar: Calendar): Calendar {
        return calculator.getOfficialSunsetCalendarForDate(currentCalendar)
    }

    fun getSunSetRiseTimes(currentCalendar: Calendar): List<Pair<SunSetRise, ZonedDateTime>> {
        val currentDayNight = calculate(currentCalendar)

        val lastSunSetRiseTime =
            if (currentDayNight == DayNight.DAY) {
                getSunRiseTime(currentCalendar)
            } else {
                if (currentCalendar.before(getSunSetTime(currentCalendar))) {
                    getSunSetTime(currentCalendar.run { (clone() as Calendar).apply { add(Calendar.DATE, -1) } })
                } else {
                    getSunSetTime(currentCalendar)
                }
            }
        val changedDate = currentCalendar.get(Calendar.DATE) != lastSunSetRiseTime.get(Calendar.DATE)

        val sunSetRiseTimes = mutableListOf<Pair<SunSetRise, ZonedDateTime>>()
        val first = if (currentDayNight == DayNight.DAY) SunSetRise.SUN_RISE else SunSetRise.SUN_SET
        val second = if (currentDayNight == DayNight.DAY) SunSetRise.SUN_SET else SunSetRise.SUN_RISE
        val third = if (second == SunSetRise.SUN_RISE) SunSetRise.SUN_SET else SunSetRise.SUN_RISE

        val firstTime = lastSunSetRiseTime.toZonedDateTime()
        val secondTime =
            if (first == SunSetRise.SUN_RISE) {
                getSunSetTime(currentCalendar).toZonedDateTime()
            } else {
                getSunRiseTime(
                    currentCalendar.run {
                        if (changedDate) {
                            this
                        } else {
                            (clone() as Calendar).apply {
                                add(
                                    Calendar.DATE,
                                    1,
                                )
                            }
                        }
                    },
                ).toZonedDateTime()
            }
        val thirdTime =
            if (second == SunSetRise.SUN_RISE) {
                getSunSetTime(
                    currentCalendar.run {
                        if (changedDate) {
                            this
                        } else {
                            (clone() as Calendar).apply {
                                add(
                                    Calendar.DATE,
                                    1,
                                )
                            }
                        }
                    },
                ).toZonedDateTime()
            } else {
                getSunRiseTime(currentCalendar.run { (clone() as Calendar).apply { add(Calendar.DATE, 1) } }).toZonedDateTime()
            }

        sunSetRiseTimes.add(first to firstTime)
        sunSetRiseTimes.add(second to secondTime)
        sunSetRiseTimes.add(third to thirdTime)
        return sunSetRiseTimes
    }

    private fun Calendar.toDay(): Triple<Int, Int, Int> = Triple(this.get(Calendar.YEAR), this.get(Calendar.MONTH), this.get(Calendar.DATE))

    enum class DayNight {
        DAY,
        NIGHT,
    }
}
