package io.github.pknujsp.everyweather.core.widgetnotification.model

import io.github.pknujsp.everyweather.core.common.manager.AppAlarmManager
import io.github.pknujsp.everyweather.core.model.notification.enums.RefreshInterval

interface ComponentServiceAutoRefreshScheduler {
    fun scheduleAutoRefresh(refreshInterval: RefreshInterval)

    fun unScheduleAutoRefresh()

    fun getScheduleState(): AppAlarmManager.ScheduledState
}
