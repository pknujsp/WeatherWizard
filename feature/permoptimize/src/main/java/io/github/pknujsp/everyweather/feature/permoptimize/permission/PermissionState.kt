package io.github.pknujsp.everyweather.feature.permoptimize.permission

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.core.common.FeatureType


sealed interface PermissionState {
    val permissionType: FeatureType.Permission

    data class Granted(override val permissionType: FeatureType.Permission) : PermissionState
    data class Denied(override val permissionType: FeatureType.Permission) : PermissionState
}

@Stable
private class MutablePermissionManager(
    permissionType: FeatureType.Permission, context: Context, val fetchPermissionStateFunc: () -> Unit
) : PermissionManager {
    override var permissionState: PermissionState by mutableStateOf(if (permissionType.isAvailable(context)) {
        PermissionState.Granted(permissionType)
    } else {
        PermissionState.Denied(permissionType)
    })
    override var isShowSettingsActivity: Boolean by mutableStateOf(false)
        private set

    override fun fetchPermissionState() {
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
interface PermissionManager {
    val permissionState: PermissionState
    val isShowSettingsActivity: Boolean
    fun fetchPermissionState()
    fun showSettingsActivity()
    fun hideSettingsActivity()
}


@Composable
fun rememberPermissionManager(
    context: Context = LocalContext.current,
    defaultPermissionType: FeatureType.Permission,
): PermissionManager {
    var fetchPermissionType by remember(defaultPermissionType) { mutableStateOf<Pair<FeatureType.Permission, Long>>(defaultPermissionType to 0) }

    val permissionManager = remember {
        MutablePermissionManager(defaultPermissionType, context, fetchPermissionStateFunc = {
            fetchPermissionType = fetchPermissionType.first to fetchPermissionType.second + 1
        })
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        permissionManager.permissionState = if (result.all { it.value }) {
            PermissionState.Granted(defaultPermissionType)
        } else {
            PermissionState.Denied(defaultPermissionType)
        }
    }

    LaunchedEffect(fetchPermissionType) {
        permissionLauncher.launch(fetchPermissionType.first.permissions)
    }
    return permissionManager
}