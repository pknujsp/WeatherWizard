package io.github.pknujsp.everyweather.feature.permoptimize.permission

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.feature.permoptimize.feature.ShowSettingsActivity
import io.github.pknujsp.everyweather.feature.permoptimize.feature.UnavailableFeatureScreen
import kotlinx.coroutines.launch

@Composable
fun PermissionStateScreen(permissionManager: PermissionStateManager) {
    if (!permissionManager.isEnabled(LocalContext.current)) {
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(Unit) {
            permissionManager.requestPermission()
        }
        Box {
            UnavailableFeatureScreen(featureType = permissionManager.featureType) {
                coroutineScope.launch {
                    permissionManager.showSettingsActivity()
                }
            }
            if (permissionManager.isShowSettingsActivity) {
                ShowSettingsActivity(permissionManager.featureType) {
                    coroutineScope.launch {
                        permissionManager.hideSettingsActivity()
                        permissionManager.requestPermission()
                    }
                }
            }
        }
    }
}