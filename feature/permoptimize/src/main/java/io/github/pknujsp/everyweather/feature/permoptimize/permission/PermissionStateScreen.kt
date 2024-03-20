package io.github.pknujsp.everyweather.feature.permoptimize.permission

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.feature.permoptimize.feature.ShowAppSettingsActivity
import io.github.pknujsp.everyweather.feature.permoptimize.feature.UnavailableFeatureScreen
import kotlinx.coroutines.launch

@Composable
fun PermissionStateScreen(permissionManager: PermissionManager) {
    if (!permissionManager.isEnabled(LocalContext.current)) {
        val coroutineScope = rememberCoroutineScope()
        Box {
            UnavailableFeatureScreen(featureType = permissionManager.permissionType) {
                coroutineScope.launch {
                    permissionManager.showSettingsActivity()
                }
            }
            if (permissionManager.isShowSettingsActivity) {
                ShowAppSettingsActivity(permissionManager.permissionType) {
                    coroutineScope.launch {
                        permissionManager.hideSettingsActivity()
                        permissionManager.requestPermission()
                    }
                }
            }
        }
    }
}