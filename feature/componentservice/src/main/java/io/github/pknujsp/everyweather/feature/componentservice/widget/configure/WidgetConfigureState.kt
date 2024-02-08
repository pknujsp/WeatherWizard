package io.github.pknujsp.everyweather.feature.componentservice.widget.configure

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.model.notification.enums.RefreshInterval
import io.github.pknujsp.everyweather.feature.permoptimize.feature.AppFeatureState
import io.github.pknujsp.everyweather.feature.permoptimize.feature.rememberAppFeatureState
import io.github.pknujsp.everyweather.feature.permoptimize.permission.PermissionManager
import io.github.pknujsp.everyweather.feature.permoptimize.permission.PermissionState
import io.github.pknujsp.everyweather.feature.permoptimize.permission.rememberPermissionManager


@Composable
fun rememberWidgetConfigureState(
    context: Context = LocalContext.current, refreshInterval: RefreshInterval
): WidgetConfigureState {
    val batteryOptimizationFeatureState = rememberAppFeatureState(featureType = FeatureType.BatteryOptimization)
    val backgroundLocationPermissionManager = rememberPermissionManager(defaultPermissionType = FeatureType.Permission.BackgroundLocation)

    val state = remember(refreshInterval) {
        MutableWidgetConfigureState(batteryOptimizationFeatureState, backgroundLocationPermissionManager)
    }

    return state
}

private class MutableWidgetConfigureState(
    override val batteryOptimizationFeatureState: AppFeatureState, permissionManager: PermissionManager
) : WidgetConfigureState {
    override val backgroundLocationPermissionState: PermissionState? by derivedStateOf { permissionManager.permissionState }
}


@Stable
interface WidgetConfigureState {
    val batteryOptimizationFeatureState: AppFeatureState
    val backgroundLocationPermissionState: PermissionState?
}