package io.github.pknujsp.weatherwizard.core.widgetnotification.model

import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval

interface ComponentServiceAutoRefreshScheduler {
    fun scheduleAutoRefresh(refreshInterval: RefreshInterval)

    fun unScheduleAutoRefresh()

    fun getScheduleState(): AppAlarmManager.ScheduledState
}