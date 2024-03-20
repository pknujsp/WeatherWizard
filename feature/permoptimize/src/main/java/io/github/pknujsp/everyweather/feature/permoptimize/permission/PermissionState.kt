package io.github.pknujsp.everyweather.feature.permoptimize.permission

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.core.common.FeatureIntent
import io.github.pknujsp.everyweather.core.common.FeatureType

@Stable
private class MutablePermissionManager(
    override val permissionType: FeatureType.Permission, val fetchPermissionStateFunc: () -> Unit
) : PermissionManager, FeatureIntent by permissionType {
    override var isShowSettingsActivity: Boolean by mutableStateOf(false)
        private set

    override fun requestPermission() {
        fetchPermissionStateFunc()
    }

    override fun showSettingsActivity() {
        isShowSettingsActivity = true
    }

    override fun hideSettingsActivity() {
        isShowSettingsActivity = false
    }
}

@Stable
interface PermissionManager : FeatureIntent {
    val isShowSettingsActivity: Boolean
    val permissionType: FeatureType.Permission
    fun requestPermission()
    fun showSettingsActivity()
    fun hideSettingsActivity()
}

@Composable
fun rememberPermissionManager(
    context: Context = LocalContext.current,
    permissionType: FeatureType.Permission,
): PermissionManager {
    var permissionResult by remember {
        mutableStateOf(permissionType.isEnabled(context))
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        permissionResult = result.all { it.value }
    }

    val permissionManager = remember {
        MutablePermissionManager(permissionType, fetchPermissionStateFunc = {
            permissionLauncher.launch(permissionType.permissions)
        })
    }

    return permissionManager
}