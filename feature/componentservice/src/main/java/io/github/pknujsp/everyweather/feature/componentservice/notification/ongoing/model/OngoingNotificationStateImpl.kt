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
import androidx.compose.runtime.snapshotFlow
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
        override val notificationUiState: OngoingNotificationUiState,
        override val batteryOptimizationState: FeatureStateManager,
        override val backgroundLocationPermissionManager: PermissionStateManager,
        override val snackbarHostState: SnackbarHostState,
) : OngoingNotificationState {
    private val ongoingNotificationAlarmManager = AppComponentServiceManagerFactory.getManager(context, AppComponentServiceManagerFactory.ONGOING_NOTIFICATION_ALARM_MANAGER)
    private val appNotificationManager = AppComponentManagerFactory.getManager(context, AppComponentManagerFactory.NOTIFICATION_MANAGER)
    override var showSearch by mutableStateOf(false)

    fun switchNotification(
            context: Context,
            refreshInterval: RefreshInterval,
    ) {
        if (notificationUiState.isEnabled) {
            context.sendBroadcast(
                    ComponentPendingIntentManager.getIntent(
                            context,
                            OngoingNotificationServiceArgument(),
                            AppComponentServiceReceiver.ACTION_REFRESH,
                    ),
            )
            ongoingNotificationAlarmManager.scheduleAutoRefresh(refreshInterval)
            return
        }
        ongoingNotificationAlarmManager.unScheduleAutoRefresh()
        appNotificationManager.cancelNotification(NotificationType.ONGOING)
    }
}

@Composable
fun rememberOngoingNotificationState(
        navController: NavController,
        ongoingNotificationUiState: OngoingNotificationUiState,
        context: Context = LocalContext.current,
): OngoingNotificationState {
    val batteryOptimizationStateManager = rememberFeatureStateManager(featureType = FeatureType.BatteryOptimization)
    val backgroundLocationPermissionManager = rememberPermissionStateManager(permissionType = FeatureType.Permission.BackgroundLocation)
    val snackbarHostState = remember { SnackbarHostState() }

    val state = remember(navController, ongoingNotificationUiState) {
        OngoingNotificationStateImpl(
                context,
                ongoingNotificationUiState,
                batteryOptimizationStateManager,
                backgroundLocationPermissionManager,
                snackbarHostState,
        )
    }

    LaunchedEffect(ongoingNotificationUiState.settings.refreshInterval) {
        val refreshInterval = ongoingNotificationUiState.settings.refreshInterval
        if (refreshInterval != RefreshInterval.MANUAL && !batteryOptimizationStateManager.isEnabled(context)) {
            showSnackbar(context, batteryOptimizationStateManager.featureType, batteryOptimizationStateManager::showSettingsActivity, snackbarHostState)
        }
    }

    LaunchedEffect(ongoingNotificationUiState.isChanged) {
        if (ongoingNotificationUiState.action == OngoingNotificationUiState.Action.DISABLED) {
            state.switchNotification(context, ongoingNotificationUiState.settings.refreshInterval)
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
        }
        if (!backgroundLocationPermissionManager.isEnabled(context)) {
            showSnackbar(
                    context,
                    backgroundLocationPermissionManager.featureType,
                    backgroundLocationPermissionManager::showSettingsActivity,
                    snackbarHostState,
            )
            return@LaunchedEffect
        }

        state.switchNotification(context, ongoingNotificationUiState.settings.refreshInterval)
        if (ongoingNotificationUiState.action == OngoingNotificationUiState.Action.UPDATED) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(batteryOptimizationStateManager.isChanged, backgroundLocationPermissionManager.isChanged) {
        if (!backgroundLocationPermissionManager.isEnabled(context) or !batteryOptimizationStateManager.isEnabled(context)) {
            return@LaunchedEffect
        }

        if (ongoingNotificationUiState.action in OngoingNotificationUiState.Action.isOn) {
            state.switchNotification(context, ongoingNotificationUiState.settings.refreshInterval)
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
    val notificationUiState: OngoingNotificationUiState
    var showSearch: Boolean
}