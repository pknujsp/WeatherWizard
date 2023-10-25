package io.github.pknujsp.weatherwizard.core.common.manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.SystemClock

class AppAlarmManager(context: Context) {
    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @SuppressLint("MissingPermission")
    fun schedule(timeInMillis: Long, pendingIntent: PendingIntent) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    fun unSchedule(pendingIntent: PendingIntent) {
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleRepeat(intervalInMillis: Long, pendingIntent: PendingIntent) {
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + intervalInMillis,
            intervalInMillis, pendingIntent)
    }

    fun unScheduleRepeat(pendingIntent: PendingIntent) {
        alarmManager.cancel(pendingIntent)
    }
}