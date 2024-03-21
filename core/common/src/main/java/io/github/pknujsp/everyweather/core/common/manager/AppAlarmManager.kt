package io.github.pknujsp.everyweather.core.common.manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock

private class AppAlarmManagerImpl(private val context: Context) : AppAlarmManager {
    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @SuppressLint("MissingPermission")
    override fun scheduleExact(
        triggerAtMillis: Long,
        pendingIntent: PendingIntent,
    ) {
        // alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        val alarmClockInfo = AlarmClockInfo(triggerAtMillis, pendingIntent)
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
    }

    override fun scheduleRepeat(
        intervalMillis: Long,
        pendingIntent: PendingIntent,
    ) {
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() - 1000L, intervalMillis, pendingIntent)
    }

    override fun unschedule(pendingIntent: PendingIntent) {
        alarmManager.cancel(pendingIntent)
    }

    override fun getScheduleState(
        requestCode: Int,
        intent: Intent,
    ): AppAlarmManager.ScheduledState =
        PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)?.run {
            AppAlarmManager.ScheduledState.Scheduled(this)
        } ?: AppAlarmManager.ScheduledState.NotScheduled
}

interface AppAlarmManager : AppComponentManager {
    companion object : AppComponentManagerInitializer {
        private var instance: AppAlarmManager? = null

        override fun getInstance(context: Context): AppAlarmManager =
            synchronized(this) {
                instance ?: AppAlarmManagerImpl(context).also { instance = it }
            }
    }

    fun scheduleExact(
        triggerAtMillis: Long,
        pendingIntent: PendingIntent,
    )

    fun scheduleRepeat(
        intervalMillis: Long,
        pendingIntent: PendingIntent,
    )

    fun unschedule(pendingIntent: PendingIntent)

    fun getScheduleState(
        requestCode: Int,
        intent: Intent,
    ): ScheduledState

    sealed interface ScheduledState {
        class Scheduled(val pendingIntent: PendingIntent) : ScheduledState

        data object NotScheduled : ScheduledState
    }
}
