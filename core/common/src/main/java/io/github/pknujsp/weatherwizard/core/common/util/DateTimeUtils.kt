package io.github.pknujsp.weatherwizard.core.common.util

import java.time.ZonedDateTime
import java.util.Calendar
import java.util.TimeZone


fun ZonedDateTime.toTimeZone(): TimeZone = TimeZone.getTimeZone(zone)

fun ZonedDateTime.toCalendar(): Calendar = Calendar.getInstance(toTimeZone())