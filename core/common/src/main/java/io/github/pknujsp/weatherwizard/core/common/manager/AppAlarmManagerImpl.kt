package io.github.pknujsp.weatherwizard.core.common.manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.SystemClock

internal class AppAlarmManagerImpl(context: Context) : AppAlarmManager {
    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @SuppressLint("MissingPermission")
    override fun schedule(timeInMillis: Long, pendingIntent: PendingIntent) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    override fun unSchedule(pendingIntent: PendingIntent) {
        alarmManager.cancel(pendingIntent)
    }

    override fun scheduleRepeat(intervalInMillis: Long, pendingIntent: PendingIntent) {
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + intervalInMillis,
            intervalInMillis,
            pendingIntent)
    }

    override fun unScheduleRepeat(pendingIntent: PendingIntent) {
        alarmManager.cancel(pendingIntent)
    }
}

interface AppAlarmManager {
    fun schedule(timeInMillis: Long, pendingIntent: PendingIntent)
    fun unSchedule(pendingIntent: PendingIntent)
    fun scheduleRepeat(intervalInMillis: Long, pendingIntent: PendingIntent)
    fun unScheduleRepeat(pendingIntent: PendingIntent)
}