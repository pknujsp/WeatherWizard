package io.github.pknujsp.weatherwizard.core.widgetnotification.model

import android.content.Context
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval

interface ComponentServiceAutoRefreshScheduler {
    fun scheduleAutoRefresh(context: Context, refreshInterval: RefreshInterval)

    fun unScheduleAutoRefresh(context: Context)

    fun getScheduleState(context: Context): AppAlarmManager.ScheduledState
}