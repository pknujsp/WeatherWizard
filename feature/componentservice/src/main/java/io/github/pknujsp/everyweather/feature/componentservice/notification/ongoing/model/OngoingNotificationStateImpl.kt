package io.github.pknujsp.everyweather.feature.componentservice.notification.ongoing.model

import android.content.Context
import androidx.compose.material3.SnackbarDuration
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
import io.github.pknujsp.everyweather.feature.permoptimize.feature.FeatureStateManager
import io.github.pknujsp.everyweather.feature.permoptimize.feature.ShowSettingsActivity
import io.github.pknujsp.everyweather.feature.permoptimize.feature.rememberFeatureStateManager
import io.github.pknujsp.everyweather.feature.permoptimize.permission.PermissionStateManager
import io.github.pknujsp.everyweather.feature.permoptimize.permission.rememberPermissionStateManager

@Stable
private class OngoingNotificationStateImpl(
    context: Context,
    private val notificationUiState: OngoingNotificationUiState,
    override val batteryOptimizationState: FeatureStateManager,
    override val backgroundLocationPermissionManager: PermissionStateManager,
    override val snackbarHostState: SnackbarHostState,
) : OngoingNotificationState {

    private val ongoingNotificationAlarmManager =
        AppComponentServiceManagerFactory.getManager(context, AppComponentServiceManagerFactory.ONGOING_NOTIFICATION_ALARM_MANAGER)
    private val appNotificationManager = AppComponentManagerFactory.getManager(context, AppComponentManagerFactory.NOTIFICATION_MANAGER)
    override var showSearch by mutableStateOf(false)

    fun notifyNotification(
        context: Context,
        refreshInterval: RefreshInterval,
    ) {
        context.sendBroadcast(
            ComponentPendingIntentManager.getIntent(
                context,
                OngoingNotificationServiceArgument(),
                AppComponentServiceReceiver.ACTION_REFRESH,
            ),
        )
        ongoingNotificationAlarmManager.scheduleAutoRefresh(refreshInterval)
    }

    fun cancelNotification() {
        ongoingNotificationAlarmManager.unScheduleAutoRefresh()
        appNotificationManager.cancelNotification(NotificationType.ONGOING)
    }

    override fun toggleShowSearch(show: Boolean) {
        showSearch = show
    }
}

@Composable
fun rememberOngoingNotificationState(
    ongoingNotificationUiState: OngoingNotificationUiState,
    context: Context = LocalContext.current,
): OngoingNotificationState {
    val batteryOptimizationStateManager = rememberFeatureStateManager(featureType = FeatureType.BatteryOptimization)
    val backgroundLocationPermissionManager = rememberPermissionStateManager(permissionType = FeatureType.Permission.BackgroundLocation)
    val snackbarHostState = remember { SnackbarHostState() }

    val state = remember(ongoingNotificationUiState) {
        OngoingNotificationStateImpl(
            context,
            ongoingNotificationUiState,
            batteryOptimizationStateManager,
            backgroundLocationPermissionManager,
            snackbarHostState,
        )
    }

    LaunchedEffect(ongoingNotificationUiState.isChanged) {
        if (ongoingNotificationUiState.action == OngoingNotificationUiState.Action.LOADING) {
            return@LaunchedEffect
        }
        if (ongoingNotificationUiState.action == OngoingNotificationUiState.Action.CHECK_UPDATE) {
            if (!ongoingNotificationUiState.isEnabled) {
                ongoingNotificationUiState.update(OngoingNotificationUiState.Action.UPDATE)
                state.cancelNotification()
                return@LaunchedEffect
            }

            if (!batteryOptimizationStateManager.isEnabled(context)) {
                showSnackbar(
                    context,
                    batteryOptimizationStateManager.featureType,
                    batteryOptimizationStateManager::showSettingsActivity,
                    snackbarHostState,
                )
                return@LaunchedEffect
            } else if (!backgroundLocationPermissionManager.isEnabled(context)) {
                showSnackbar(
                    context,
                    backgroundLocationPermissionManager.featureType,
                    backgroundLocationPermissionManager::showSettingsActivity,
                    snackbarHostState,
                )
                return@LaunchedEffect
            }

            ongoingNotificationUiState.update(OngoingNotificationUiState.Action.UPDATE)
            state.notifyNotification(context, ongoingNotificationUiState.settings.refreshInterval)
        }
    }

    if (batteryOptimizationStateManager.isShowSettingsActivity) {
        ShowSettingsActivity(featureType = batteryOptimizationStateManager.featureType) {
            batteryOptimizationStateManager.hideSettingsActivity()
        }
    } else if (backgroundLocationPermissionManager.isShowSettingsActivity) {
        ShowSettingsActivity(featureType = backgroundLocationPermissionManager.featureType) {
            backgroundLocationPermissionManager.hideSettingsActivity()
            backgroundLocationPermissionManager.requestPermission()
        }
    }

    return state
}

private suspend fun showSnackbar(
    context: Context,
    featureType: FeatureType,
    showSettingsActivity: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    when (snackbarHostState.showSnackbar(
        message = context.getString(featureType.message),
        actionLabel = context.getString(featureType.action),
        duration = SnackbarDuration.Short,
    )) {
        SnackbarResult.ActionPerformed -> {
            showSettingsActivity()
        }

        SnackbarResult.Dismissed -> {
        }
    }
}

@Stable
interface OngoingNotificationState {
    val batteryOptimizationState: FeatureStateManager
    val backgroundLocationPermissionManager: PermissionStateManager
    val snackbarHostState: SnackbarHostState
    val showSearch: Boolean
    fun toggleShowSearch(show: Boolean)
}