package io.github.pknujsp.everyweather.feature.permoptimize.permission

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import io.github.pknujsp.everyweather.core.common.PermissionType
import io.github.pknujsp.everyweather.core.common.asActivity
import io.github.pknujsp.everyweather.core.common.asFeatureType
import io.github.pknujsp.everyweather.feature.permoptimize.feature.ShowAppSettingsActivity
import io.github.pknujsp.everyweather.feature.permoptimize.feature.UnavailableFeatureScreen
import kotlinx.coroutines.launch

@Composable
fun PermissionStateScreen(permissionType: PermissionType, onGranted: () -> Unit) {
    var openSettingsActivity by remember { mutableStateOf(false) }
    val permissionManager = rememberPermissionManager(defaultPermissionType = permissionType)
    val coroutineScope = rememberCoroutineScope()
    val grantedCallback by rememberUpdatedState(newValue = onGranted)

    when (permissionManager.permissionState) {
        is PermissionState.Granted -> grantedCallback()
        is PermissionState.Denied -> {
            Box {
                UnavailableFeatureScreen(featureType = permissionManager.permissionState!!.permissionType) {
                    coroutineScope.launch {
                        permissionManager.fetchPermissionState()
                    }
                }
                if (openSettingsActivity) {
                    ShowAppSettingsActivity(permissionManager.permissionState!!.permissionType.asFeatureType()) {
                        openSettingsActivity = false
                        coroutineScope.launch {
                            permissionManager.fetchPermissionState()
                        }
                    }
                }
            }
        }

        else -> {}
    }
}