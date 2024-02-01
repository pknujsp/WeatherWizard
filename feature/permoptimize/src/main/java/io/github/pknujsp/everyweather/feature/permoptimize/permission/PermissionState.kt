package io.github.pknujsp.everyweather.feature.permoptimize.permission

import android.app.Activity
import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
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
import io.github.pknujsp.everyweather.core.common.asActivity
import io.github.pknujsp.everyweather.core.common.asFeatureType


sealed interface PermissionState {
    val permissionType: PermissionType

    data class Granted(override val permissionType: PermissionType) : PermissionState
    data class Denied(override val permissionType: PermissionType) : PermissionState
    data class ShouldShowRationale(override val permissionType: PermissionType) : PermissionState
}

private class MutablePermissionManager(
    permissionType: PermissionType, context: Context, val fetchPermissionStateFunc: () -> Unit
) : PermissionManager {
    override var permissionState: PermissionState? by mutableStateOf(if (permissionType.asFeatureType()
            .isAvailable(context)) PermissionState.Granted(permissionType) else PermissionState.Denied(permissionType))
    override var isShowSettingsActivity: Boolean by mutableStateOf(false)

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
    val permissionState: PermissionState?
    val isShowSettingsActivity: Boolean
    fun fetchPermissionState()
    fun showSettingsActivity()
    fun hideSettingsActivity()
}


@Composable
fun rememberPermissionManager(
    context: Context = LocalContext.current,
    defaultPermissionType: PermissionType,
): PermissionManager {
    val activity = context.asActivity()!!
    var fetchPermissionType by remember(defaultPermissionType) { mutableStateOf<Pair<PermissionType, Long>>(defaultPermissionType to 0) }

    val permissionManager = remember {
        MutablePermissionManager(defaultPermissionType, context, fetchPermissionStateFunc = {
            fetchPermissionType = fetchPermissionType.first to fetchPermissionType.second + 1
        })
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        permissionManager.permissionState = if (result.all { it.value }) {
            PermissionState.Granted(fetchPermissionType.first)
        } else if (activity.shouldShowRequestPermissionRationale(result.entries.first { !it.value }.key)) {
            PermissionState.ShouldShowRationale(fetchPermissionType.first)
        } else {
            PermissionState.Denied(fetchPermissionType.first)
        }
    }

    LaunchedEffect(fetchPermissionType) {
        if (fetchPermissionType.second > 0) {
            permissionLauncher.launch(fetchPermissionType.first.permissions)
        }
        //permissionManager.permissionState = fetchPermission(activity, fetchPermissionType.first, permissionLauncher)
    }
    return permissionManager
}

private fun fetchPermission(
    activity: Activity,
    permissionType: PermissionType,
    activityResultLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>
) = when {
    activity.checkSelfPermission(permissionType) -> PermissionState.Granted(permissionType)
    // activity.shouldShowRequestPermissionRationale(permissionType) -> PermissionState.ShouldShowRationale(permissionType)
    else -> {
        // activityResultLauncher.launch(permissionType.permissions)
        PermissionState.Denied(permissionType)
    }
}