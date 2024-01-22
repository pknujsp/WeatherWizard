package io.github.pknujsp.weatherwizard.feature.componentservice.notification.ongoing.model

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.weatherwizard.core.common.NotificationType
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.OngoingNotificationServiceArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.AppNotificationManager
import io.github.pknujsp.weatherwizard.feature.componentservice.ComponentPendingIntentManager
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.ongoing.OngoingNotificationAutoRefreshScheduler

class OngoingNotificationState(
    val ongoingNotificationUiState: OngoingNotificationUiState,
    private val appNotificationManager: AppNotificationManager,
    private val appAlarmManager: AppAlarmManager,
) {
    var showSearch by mutableStateOf(false)

    fun onChangedSettings(context: Context, refreshInterval: RefreshInterval, popBackStack: () -> Unit) {
        if (ongoingNotificationUiState.action == OngoingNotificationUiState.Action.NONE) {
            return
        }
        switchNotification(context, refreshInterval)

        if (ongoingNotificationUiState.action == OngoingNotificationUiState.Action.UPDATED) {
            popBackStack()
        }
    }

    private fun switchNotification(
        context: Context, refreshInterval: RefreshInterval
    ) {
        val scheduler = OngoingNotificationAutoRefreshScheduler()

        if (ongoingNotificationUiState.isEnabled) {
            context.sendBroadcast(ComponentPendingIntentManager.getIntent(context, OngoingNotificationServiceArgument()))
            scheduler.scheduleAutoRefresh(context, appAlarmManager, refreshInterval)
        } else {
            scheduler.unScheduleAutoRefresh(context, appAlarmManager)
            appNotificationManager.cancelNotification(NotificationType.ONGOING)
        }
    }
}

@Composable
fun rememberOngoingNotificationState(
    ongoingNotificationUiState: OngoingNotificationUiState, appAlarmManager: AppAlarmManager, context: Context = LocalContext.current
) = remember(ongoingNotificationUiState, context) {
    OngoingNotificationState(ongoingNotificationUiState, AppNotificationManager(context), appAlarmManager)
}