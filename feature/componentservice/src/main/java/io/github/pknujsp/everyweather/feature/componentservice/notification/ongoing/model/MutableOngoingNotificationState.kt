package io.github.pknujsp.everyweather.feature.componentservice.notification.ongoing.model

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.common.NotificationType
import io.github.pknujsp.everyweather.core.common.manager.AppComponentManagerFactory
import io.github.pknujsp.everyweather.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.everyweather.core.widgetnotification.model.OngoingNotificationServiceArgument
import io.github.pknujsp.everyweather.feature.componentservice.AppComponentServiceReceiver
import io.github.pknujsp.everyweather.feature.componentservice.ComponentPendingIntentManager
import io.github.pknujsp.everyweather.feature.componentservice.manager.AppComponentServiceManagerFactory
import io.github.pknujsp.everyweather.feature.permoptimize.feature.AppFeatureState
import io.github.pknujsp.everyweather.feature.permoptimize.feature.rememberAppFeatureState

private class MutableOngoingNotificationState(
    context: Context,
    override val ongoingNotificationUiState: OngoingNotificationUiState,
    private val featureState: AppFeatureState,
    override val snackbarHostState: SnackbarHostState = SnackbarHostState()
) : OngoingNotificationState {
    private val ongoingNotificationAlarmManager =
        AppComponentServiceManagerFactory.getManager(context, AppComponentServiceManagerFactory.ONGOING_NOTIFICATION_ALARM_MANAGER)
    private val appNotificationManager = AppComponentManagerFactory.getManager(context, AppComponentManagerFactory.NOTIFICATION_MANAGER)

    override var showSearch by mutableStateOf(false)

    override fun onChangedSettings(context: Context, refreshInterval: RefreshInterval, popBackStack: () -> Unit) {
        if (ongoingNotificationUiState.action == OngoingNotificationUiState.Action.NONE) {
            return
        }
        if (switchNotification(context,
                refreshInterval) && ongoingNotificationUiState.action == OngoingNotificationUiState.Action.UPDATED) {
            popBackStack()
        }
    }

    private fun switchNotification(
        context: Context, refreshInterval: RefreshInterval
    ): Boolean {
        if (ongoingNotificationUiState.isEnabled) {
            if (!featureState.isAvailable && refreshInterval != RefreshInterval.MANUAL) {
                snackbarHostState.showSnackbar(message = featureState.featureType.message)
                return false
            }

            context.sendBroadcast(ComponentPendingIntentManager.getIntent(context,
                OngoingNotificationServiceArgument(),
                AppComponentServiceReceiver.ACTION_REFRESH))
            ongoingNotificationAlarmManager.scheduleAutoRefresh(refreshInterval)
        } else {
            ongoingNotificationAlarmManager.unScheduleAutoRefresh()
            appNotificationManager.cancelNotification(NotificationType.ONGOING)
        }

        return true
    }
}

@Composable
fun rememberOngoingNotificationState(
    ongoingNotificationUiState: OngoingNotificationUiState, context: Context = LocalContext.current
): OngoingNotificationState {
    val featureState = rememberAppFeatureState(featureType = FeatureType.BATTERY_OPTIMIZATION)
    val state = remember(ongoingNotificationUiState) {
        MutableOngoingNotificationState(context, ongoingNotificationUiState, featureState)
    }
    LaunchedEffect(ongoingNotificationUiState.ongoingNotificationSettings.refreshInterval) {
        val refreshInterval = ongoingNotificationUiState.ongoingNotificationSettings.refreshInterval
        if (refreshInterval != RefreshInterval.MANUAL && !featureState.isAvailable) {
            // ongoingNotificationUiState.updateState(OngoingNotificationUiState.Action.SWITCH_STOPPED, FeatureType.BATTERY_OPTIMIZATION)
        }
    }
    return state
}


@Stable
interface OngoingNotificationState {
    val snackbarHostState: SnackbarHostState
    val ongoingNotificationUiState: OngoingNotificationUiState
    val showSearch: Boolean
    fun onChangedSettings(context: Context, refreshInterval: RefreshInterval, popBackStack: () -> Unit)
}