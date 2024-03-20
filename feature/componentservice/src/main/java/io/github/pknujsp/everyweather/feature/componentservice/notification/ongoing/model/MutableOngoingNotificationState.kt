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
import io.github.pknujsp.everyweather.feature.permoptimize.feature.AppFeatureState
import io.github.pknujsp.everyweather.feature.permoptimize.feature.ShowAppSettingsActivity
import io.github.pknujsp.everyweather.feature.permoptimize.feature.rememberAppFeatureState
import io.github.pknujsp.everyweather.feature.permoptimize.permission.PermissionManager
import io.github.pknujsp.everyweather.feature.permoptimize.permission.rememberPermissionManager

private class MutableOngoingNotificationState(
    context: Context,
    override val notificationUiState: OngoingNotificationUiState,
    override val batteryOptimizationState: AppFeatureState,
    override val backgroundLocationPermissionManager: PermissionManager,
    override val snackbarHostState: SnackbarHostState,
) : OngoingNotificationState {
    private val ongoingNotificationAlarmManager =
        AppComponentServiceManagerFactory.getManager(context, AppComponentServiceManagerFactory.ONGOING_NOTIFICATION_ALARM_MANAGER)
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
        } else {
            ongoingNotificationAlarmManager.unScheduleAutoRefresh()
            appNotificationManager.cancelNotification(NotificationType.ONGOING)
        }
    }
}

@Composable
fun rememberOngoingNotificationState(
    navController: NavController,
    ongoingNotificationUiState: OngoingNotificationUiState,
    context: Context = LocalContext.current,
): OngoingNotificationState {
    val batteryOptimizationState = rememberAppFeatureState(featureType = FeatureType.BatteryOptimization)
    val backgroundLocationPermissionManager = rememberPermissionManager(permissionType = FeatureType.Permission.BackgroundLocation)
    val snackbarHostState = remember { SnackbarHostState() }

    val state =
        remember(navController, ongoingNotificationUiState) {
            MutableOngoingNotificationState(
                context,
                ongoingNotificationUiState,
                batteryOptimizationState,
                backgroundLocationPermissionManager,
                snackbarHostState,
            )
        }

    LaunchedEffect(ongoingNotificationUiState.settings.refreshInterval) {
        val refreshInterval = ongoingNotificationUiState.settings.refreshInterval
        if (refreshInterval != RefreshInterval.MANUAL && !batteryOptimizationState.isAvailable(context)) {
            showSnackbar(context, batteryOptimizationState.featureType, batteryOptimizationState::showSettingsActivity, snackbarHostState)
        }
    }
    LaunchedEffect(ongoingNotificationUiState.action, ongoingNotificationUiState.changedCount) {
        if (ongoingNotificationUiState.action == OngoingNotificationUiState.Action.DISABLED) {
            state.switchNotification(context, ongoingNotificationUiState.settings.refreshInterval)
        } else if (ongoingNotificationUiState.action != OngoingNotificationUiState.Action.NONE) {
            if (!batteryOptimizationState.isAvailable(context) && ongoingNotificationUiState.settings.refreshInterval != RefreshInterval.MANUAL) {
                showSnackbar(
                    context,
                    batteryOptimizationState.featureType,
                    batteryOptimizationState::showSettingsActivity,
                    snackbarHostState,
                )
            } else if (!backgroundLocationPermissionManager.isEnabled(context)) {
                showSnackbar(
                    context,
                    backgroundLocationPermissionManager.permissionType,
                    backgroundLocationPermissionManager::showSettingsActivity,
                    snackbarHostState,
                )
            } else {
                state.switchNotification(context, ongoingNotificationUiState.settings.refreshInterval)
                if (ongoingNotificationUiState.action == OngoingNotificationUiState.Action.UPDATED) {
                    navController.popBackStack()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { batteryOptimizationState.isAvailable(context) }.collect { ignoredBatteryOptimization ->
            if (backgroundLocationPermissionManager.isEnabled(context) && (ongoingNotificationUiState.settings.refreshInterval == RefreshInterval.MANUAL || ignoredBatteryOptimization)) {
                if (ongoingNotificationUiState.action == OngoingNotificationUiState.Action.UPDATED || ongoingNotificationUiState.action == OngoingNotificationUiState.Action.ENABLED) {
                    state.switchNotification(context, ongoingNotificationUiState.settings.refreshInterval)
                }
            }
        }
    }

    if (batteryOptimizationState.isShowSettingsActivity) {
        ShowAppSettingsActivity(featureType = batteryOptimizationState.featureType) {
            batteryOptimizationState.hideSettingsActivity()
        }
    }
    if (backgroundLocationPermissionManager.isShowSettingsActivity) {
        ShowAppSettingsActivity(featureType = backgroundLocationPermissionManager.permissionType) {
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
    when (
        snackbarHostState.showSnackbar(
            message = context.getString(featureType.message),
            actionLabel = context.getString(featureType.action),
            duration = SnackbarDuration.Short,
        )
    ) {
        SnackbarResult.ActionPerformed -> {
            showSettingsActivity()
        }

        SnackbarResult.Dismissed -> {
        }
    }
}

@Stable
interface OngoingNotificationState {
    val batteryOptimizationState: AppFeatureState
    val backgroundLocationPermissionManager: PermissionManager
    val snackbarHostState: SnackbarHostState
    val notificationUiState: OngoingNotificationUiState
    var showSearch: Boolean
}
