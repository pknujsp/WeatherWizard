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
import io.github.pknujsp.everyweather.core.common.manager.PermissionType
import io.github.pknujsp.everyweather.core.common.manager.checkSelfPermission
import io.github.pknujsp.everyweather.core.common.manager.shouldShowRequestPermissionRationale


sealed interface PermissionState {
    val permissionType: PermissionType

    data class Granted(override val permissionType: PermissionType) : PermissionState
    data class Denied(override val permissionType: PermissionType) : PermissionState
    data class ShouldShowRationale(override val permissionType: PermissionType) : PermissionState
}

private class MutablePermissionManager(
    val fetchPermissionStateFunc: () -> Unit
) : PermissionManager {
    override var permissionState: PermissionState? by mutableStateOf(null)

    override fun fetchPermissionState() {
        fetchPermissionStateFunc()
    }
}

@Stable
interface PermissionManager {
    val permissionState: PermissionState?
    fun fetchPermissionState()
}


@Composable
fun rememberPermissionManager(
    context: Context = LocalContext.current,
    defaultPermissionType: PermissionType,
): PermissionManager {
    val activity = context.asActivity()!!
    var fetchPermissionType by remember(defaultPermissionType) { mutableStateOf<Pair<PermissionType, Long>>(defaultPermissionType to 0) }

    val permissionManager = remember {
        MutablePermissionManager(fetchPermissionStateFunc = {
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
        permissionManager.permissionState = fetchPermission(activity, fetchPermissionType.first, permissionLauncher)
    }
    return permissionManager
}

private fun fetchPermission(
    activity: Activity,
    permissionType: PermissionType,
    activityResultLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>
) = when {
    activity.checkSelfPermission(permissionType) -> PermissionState.Granted(permissionType)
    activity.shouldShowRequestPermissionRationale(permissionType) -> PermissionState.ShouldShowRationale(permissionType)
    else -> {
        activityResultLauncher.launch(permissionType.permissions)
        PermissionState.Denied(permissionType)
    }
}