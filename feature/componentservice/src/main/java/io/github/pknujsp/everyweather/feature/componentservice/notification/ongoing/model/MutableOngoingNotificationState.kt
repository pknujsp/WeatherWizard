package io.github.pknujsp.everyweather.feature.componentservice.notification.ongoing.model

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.common.NotificationType
import io.github.pknujsp.everyweather.core.common.manager.AppComponentManagerFactory
import io.github.pknujsp.everyweather.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.everyweather.core.widgetnotification.model.OngoingNotificationServiceArgument
import io.github.pknujsp.everyweather.feature.componentservice.AppComponentServiceReceiver
import io.github.pknujsp.everyweather.feature.componentservice.ComponentPendingIntentManager
import io.github.pknujsp.everyweather.feature.componentservice.manager.AppComponentServiceManagerFactory
import io.github.pknujsp.everyweather.feature.permoptimize.feature.AppFeatureState
import io.github.pknujsp.everyweather.feature.permoptimize.feature.ShowAppSettingsActivity
import io.github.pknujsp.everyweather.feature.permoptimize.feature.rememberAppFeatureState

private class MutableOngoingNotificationState(
    context: Context,
    override val ongoingNotificationUiState: OngoingNotificationUiState,
    override val featureState: AppFeatureState,
    override val snackbarHostState: SnackbarHostState
) : OngoingNotificationState {
    private val ongoingNotificationAlarmManager =
        AppComponentServiceManagerFactory.getManager(context, AppComponentServiceManagerFactory.ONGOING_NOTIFICATION_ALARM_MANAGER)
    private val appNotificationManager = AppComponentManagerFactory.getManager(context, AppComponentManagerFactory.NOTIFICATION_MANAGER)

    override var showSearch by mutableStateOf(false)

    fun switchNotification(
        context: Context, refreshInterval: RefreshInterval
    ): Boolean {
        if (ongoingNotificationUiState.isEnabled) {
            if (!featureState.isAvailable && refreshInterval != RefreshInterval.MANUAL) {
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
    navController: NavController, ongoingNotificationUiState: OngoingNotificationUiState, context: Context = LocalContext.current
): OngoingNotificationState {
    val featureState = rememberAppFeatureState(featureType = FeatureType.BATTERY_OPTIMIZATION)
    val snackbarHostState = remember { SnackbarHostState() }

    val state = remember(navController, ongoingNotificationUiState) {
        MutableOngoingNotificationState(context, ongoingNotificationUiState, featureState, snackbarHostState)
    }

    LaunchedEffect(ongoingNotificationUiState.ongoingNotificationSettings.refreshInterval) {
        val refreshInterval = ongoingNotificationUiState.ongoingNotificationSettings.refreshInterval
        if (refreshInterval != RefreshInterval.MANUAL && !featureState.isAvailable) {
            showSnackbar(context, featureState.featureType, featureState, snackbarHostState)
        }
    }
    LaunchedEffect(ongoingNotificationUiState.action, ongoingNotificationUiState.changedCount) {
        if (ongoingNotificationUiState.action != OngoingNotificationUiState.Action.NONE) {
            if (state.switchNotification(context, ongoingNotificationUiState.ongoingNotificationSettings.refreshInterval)) {
                if (ongoingNotificationUiState.action == OngoingNotificationUiState.Action.UPDATED) {
                    navController.popBackStack()
                }
            } else {
                showSnackbar(context, featureState.featureType, featureState, snackbarHostState)
            }
        }
    }

    if (featureState.isShowSettingsActivity) {
        ShowAppSettingsActivity(featureType = featureState.featureType) {
            featureState.hideSettingsActivity()
        }
    }
    return state
}


private suspend fun showSnackbar(
    context: Context, featureType: FeatureType, featureState: AppFeatureState, snackbarHostState: SnackbarHostState
) {
    when (snackbarHostState.showSnackbar(message = context.getString(featureType.message))) {
        SnackbarResult.ActionPerformed -> {
            featureState.showSettingsActivity()
        }

        SnackbarResult.Dismissed -> {
        }
    }
}

@Stable
interface OngoingNotificationState {
    val featureState: AppFeatureState
    val snackbarHostState: SnackbarHostState
    val ongoingNotificationUiState: OngoingNotificationUiState
    var showSearch: Boolean
}