package io.github.pknujsp.everyweather.core.ui.feature

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import io.github.pknujsp.everyweather.core.common.asFeatureType
import io.github.pknujsp.everyweather.core.common.manager.PermissionState
import io.github.pknujsp.everyweather.core.common.manager.PermissionType
import io.github.pknujsp.everyweather.core.common.manager.rememberPermissionManager
import kotlinx.coroutines.launch

@Composable
fun PermissionStateScreen(permissionType: PermissionType, onGranted: () -> Unit) {
    var openSettingsActivity by remember { mutableStateOf(false) }
    val permissionManager = rememberPermissionManager(defaultPermissionType = permissionType)
    val coroutineScope = rememberCoroutineScope()
    val grantedCallback by rememberUpdatedState(newValue = onGranted)

    Box {
        when (permissionManager.permissionState) {
            is PermissionState.Granted -> grantedCallback()
            is PermissionState.Denied, is PermissionState.ShouldShowRationale -> {
                UnavailableFeatureScreen(featureType = permissionManager.permissionState!!.permissionType) {
                    openSettingsActivity = true
                }
                if (openSettingsActivity) {
                    OpenAppSettingsActivity(permissionManager.permissionState!!.permissionType.asFeatureType()) {
                        openSettingsActivity = false
                        coroutineScope.launch {
                            permissionManager.fetchPermissionState()
                        }
                    }
                }
            }

            else -> {}
        }
    }
}