package io.github.pknujsp.weatherwizard.core.common.util

import java.time.ZonedDateTime
import java.util.Calendar
import java.util.TimeZone


fun ZonedDateTime.toTimeZone(): TimeZone = TimeZone.getTimeZone(zone)

fun ZonedDateTime.toCalendar(): Calendar = Calendar.Builder()
    .setTimeZone(toTimeZone())
    .setFields(
        Calendar.YEAR, year,
        Calendar.MONTH, monthValue - 1,
        Calendar.DATE, dayOfMonth,
        Calendar.HOUR_OF_DAY, hour,
        Calendar.MINUTE, minute,
        Calendar.SECOND, second,
    ).build()

fun Calendar.toZonedDateTime(): ZonedDateTime = toInstant().atZone(timeZone.toZoneId())