package io.github.pknujsp.weatherwizard.feature.notification.ongoing.model

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.weatherwizard.core.common.NotificationType
import io.github.pknujsp.weatherwizard.core.common.manager.AppAlarmManager
import io.github.pknujsp.weatherwizard.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.OngoingNotificationServiceArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.NotificationServiceReceiver
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.AppNotificationManager
import io.github.pknujsp.weatherwizard.core.widgetnotification.notification.NotificationService

class OngoingNotificationState(
    val ongoingNotificationUiState: OngoingNotificationUiState,
    private val appNotificationManager: AppNotificationManager,
    private val appAlarmManager: AppAlarmManager,
) {
    var showSearch by mutableStateOf(false)

    fun onChangedSettings(context: Context) {
        if (ongoingNotificationUiState.action == OngoingNotificationUiState.Action.NONE) {
            return
        }
        switchNotification(context)

        when (ongoingNotificationUiState.action) {
            OngoingNotificationUiState.Action.ENABLED -> {}
            OngoingNotificationUiState.Action.UPDATED -> {}
            OngoingNotificationUiState.Action.DISABLED -> {}
            else -> {
            }
        }
    }

    private fun switchNotification(
        context: Context
    ) {
        val pendingIntent = appNotificationManager.getRefreshPendingIntent(context,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            ComponentServiceAction.OngoingNotification())

        if (ongoingNotificationUiState.ongoingNotificationSettings.refreshInterval != RefreshInterval.MANUAL) {
            appAlarmManager.unScheduleRepeat(pendingIntent)
        }

        if (ongoingNotificationUiState.isEnabled) {
            val intent = Intent(context, NotificationServiceReceiver::class.java).apply {
                action = NotificationService.ACTION_PROCESS
                putExtras(OngoingNotificationServiceArgument().toBundle())
            }
            context.sendBroadcast(intent)
            if (ongoingNotificationUiState.ongoingNotificationSettings.refreshInterval != RefreshInterval.MANUAL) {
                appAlarmManager.scheduleRepeat(ongoingNotificationUiState.ongoingNotificationSettings.refreshInterval.interval,
                    pendingIntent)
            }
        } else {
            appNotificationManager.cancelNotification(NotificationType.ONGOING)
            pendingIntent.cancel()
        }
    }
}

@Composable
fun rememberOngoingNotificationState(
    ongoingNotificationUiState: OngoingNotificationUiState, appAlarmManager: AppAlarmManager, context: Context = LocalContext.current
) = remember(ongoingNotificationUiState, context) {
    OngoingNotificationState(ongoingNotificationUiState, AppNotificationManager(context), appAlarmManager)
}