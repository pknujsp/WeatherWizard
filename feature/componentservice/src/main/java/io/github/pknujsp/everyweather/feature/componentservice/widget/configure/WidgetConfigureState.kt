package io.github.pknujsp.everyweather.feature.componentservice.widget.configure

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.feature.permoptimize.feature.FeatureStateManager
import io.github.pknujsp.everyweather.feature.permoptimize.feature.ShowSettingsActivity
import io.github.pknujsp.everyweather.feature.permoptimize.feature.rememberFeatureStateManager
import io.github.pknujsp.everyweather.feature.permoptimize.permission.PermissionStateManager
import io.github.pknujsp.everyweather.feature.permoptimize.permission.rememberPermissionStateManager

@Composable
fun rememberWidgetConfigureState(): WidgetConfigureState {
    val batteryOptimizationFeatureState = rememberFeatureStateManager(featureType = FeatureType.BatteryOptimization)
    val backgroundLocationPermissionManager = rememberPermissionStateManager(permissionType = FeatureType.Permission.BackgroundLocation)
    val snackHostState = remember { SnackbarHostState() }

    val state = remember {
        MutableWidgetConfigureState(snackHostState, batteryOptimizationFeatureState, backgroundLocationPermissionManager)
    }

    if (batteryOptimizationFeatureState.isShowSettingsActivity) {
        ShowSettingsActivity(FeatureType.BatteryOptimization) {
            state.batteryOptimizationFeatureState.hideSettingsActivity()
        }
    } else if (backgroundLocationPermissionManager.isShowSettingsActivity) {
        ShowSettingsActivity(FeatureType.Permission.BackgroundLocation) {
            backgroundLocationPermissionManager.hideSettingsActivity()
            backgroundLocationPermissionManager.requestPermission()
        }
    }

    return state
}

@Stable
private class MutableWidgetConfigureState(
        override val snackHostState: SnackbarHostState,
        override val batteryOptimizationFeatureState: FeatureStateManager,
        override val backgroundLocationPermissionManager: PermissionStateManager,
) : WidgetConfigureState {
    override suspend fun showSnackbar(
            context: Context,
            message: Int,
            action: Int?,
            showSettingsActivity: (() -> Unit)?,
    ) {
        if (action == null) {
            snackHostState.showSnackbar(message = context.getString(message), duration = SnackbarDuration.Short)
        } else {
            when (snackHostState.showSnackbar(
                    message = context.getString(message),
                    actionLabel = context.getString(action),
                    duration = SnackbarDuration.Short,
            )) {
                SnackbarResult.ActionPerformed -> {
                    showSettingsActivity?.invoke()
                }

                SnackbarResult.Dismissed -> {
                }
            }
        }
    }
}

@Stable
interface WidgetConfigureState {
    val batteryOptimizationFeatureState: FeatureStateManager
    val backgroundLocationPermissionManager: PermissionStateManager
    val snackHostState: SnackbarHostState

    suspend fun showSnackbar(
            context: Context,
            message: Int,
            action: Int? = null,
            showSettingsActivity: (() -> Unit)? = null,
    )
}