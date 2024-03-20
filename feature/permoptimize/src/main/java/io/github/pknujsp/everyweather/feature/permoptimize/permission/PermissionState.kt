package io.github.pknujsp.everyweather.feature.permoptimize.permission

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.feature.permoptimize.BaseFeatureStateManager

@Stable
private class MutablePermissionStateManager(
    override val featureType: FeatureType.Permission,
    val fetchPermissionStateFunc: () -> Unit,
) : PermissionStateManager() {
    override fun requestPermission() {
        fetchPermissionStateFunc()
    }
}

@Stable
abstract class PermissionStateManager() : BaseFeatureStateManager() {
    abstract fun requestPermission()
}

@Composable
fun rememberPermissionStateManager(
    permissionType: FeatureType.Permission,
): PermissionStateManager {
    var permissionState by remember {
        mutableIntStateOf(0)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        permissionState++
    }

    val permissionManager = remember {
        MutablePermissionStateManager(permissionType, fetchPermissionStateFunc = {
            permissionLauncher.launch(permissionType.permissions)
        })
    }

    LaunchedEffect(permissionState) {
        if (permissionState > 0) {
            permissionManager.onChanged()
        }
    }

    return permissionManager
}