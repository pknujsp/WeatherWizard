package io.github.pknujsp.weatherwizard.feature.componentservice.notification.ongoing.model

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.weatherwizard.core.common.NotificationType
import io.github.pknujsp.weatherwizard.core.common.manager.AppComponentManagerFactory
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.OngoingNotificationServiceArgument
import io.github.pknujsp.weatherwizard.feature.componentservice.AppComponentServiceReceiver
import io.github.pknujsp.weatherwizard.feature.componentservice.ComponentPendingIntentManager
import io.github.pknujsp.weatherwizard.feature.componentservice.manager.AppComponentServiceManagerFactory

@Stable
class OngoingNotificationState(
    context: Context,
    val ongoingNotificationUiState: OngoingNotificationUiState,
) {
    private val ongoingNotificationAlarmManager =
        AppComponentServiceManagerFactory.getManager(context, AppComponentServiceManagerFactory.ONGOING_NOTIFICATION_ALARM_MANAGER)
    private val appNotificationManager = AppComponentManagerFactory.getManager(context, AppComponentManagerFactory.NOTIFICATION_MANAGER)
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
        if (ongoingNotificationUiState.isEnabled) {
            context.sendBroadcast(ComponentPendingIntentManager.getIntent(context,
                OngoingNotificationServiceArgument(),
                AppComponentServiceReceiver.ACTION_REFRESH))
            ongoingNotificationAlarmManager.scheduleAutoRefresh(refreshInterval)
        } else {
            ongoingNotificationAlarmManager.unScheduleAutoRefresh()
            appNotificationManager.cancelNotification(NotificationType.ONGOING)
        }
    }
}

@Composable
fun rememberOngoingNotificationState(
    ongoingNotificationUiState: OngoingNotificationUiState, context: Context = LocalContext.current
) = remember(ongoingNotificationUiState) {
    OngoingNotificationState(context, ongoingNotificationUiState)
}