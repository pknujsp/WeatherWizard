package io.github.pknujsp.weatherwizard.core.common.manager

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.github.pknujsp.weatherwizard.core.common.StatefulFeature
import io.github.pknujsp.weatherwizard.core.common.asActivity

sealed interface PermissionState {
    val permissionType: PermissionType

    data class Granted(override val permissionType: PermissionType) : PermissionState
    data class Denied(override val permissionType: PermissionType) : PermissionState
    data class ShouldShowRationale(override val permissionType: PermissionType) : PermissionState
}

private class PermissionManagerImpl(
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
        PermissionManagerImpl(fetchPermissionStateFunc = {
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


enum class PermissionType(val permissions: Array<String>, val isUnrelatedSdkDevice: Boolean) : StatefulFeature {
    LOCATION(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), isUnrelatedSdkDevice = false) {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.location_permission
        override val message: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.location_permission_denied
        override val action: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.grant_permissions
        override val hasRepairAction: Boolean = true
    },
    BACKGROUND_LOCATION(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    } else {
        emptyArray()
    }, isUnrelatedSdkDevice = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.background_location_permission
        override val message: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.background_location_permission_denied
        override val action: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.grant_permissions
        override val hasRepairAction: Boolean = true
    },
    STORAGE(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }, isUnrelatedSdkDevice = false) {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.storage_permission
        override val message: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.storage_permission_denied
        override val action: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.grant_permissions
        override val hasRepairAction: Boolean = true
    },

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    POST_NOTIFICATIONS(arrayOf(Manifest.permission.POST_NOTIFICATIONS),
        isUnrelatedSdkDevice = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.post_notification_permission
        override val message: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.post_notification_permission_denied
        override val action: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.grant_permissions
        override val hasRepairAction: Boolean = true
    },

    @RequiresApi(Build.VERSION_CODES.S)
    SCHEDULE_EXACT_ALARM_ABOVE_EQUALS_ON_SDK_31(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.SCHEDULE_EXACT_ALARM,
            Manifest.permission.USE_EXACT_ALARM,
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.SCHEDULE_EXACT_ALARM,
        )
    } else {
        emptyArray()
    }, isUnrelatedSdkDevice = Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.exact_alarm_permission
        override val message: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.exact_alarm_permission_denied
        override val action: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.grant_permissions
        override val hasRepairAction: Boolean = true
    };
}

/**
 * 해당 권한을 부여해야 하는 이유를 설명해야 하는지 확인
 */
private fun Activity.shouldShowRequestPermissionRationale(permissionType: PermissionType): Boolean =
    permissionType.isUnrelatedSdkDevice or permissionType.permissions.any { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }


/**
 * 해당 권한을 고려할 필요가 없는 SDK 버전이라면 즉시 true를 반환
 */
fun Context.checkSelfPermission(permissionType: PermissionType): Boolean =
    permissionType.isUnrelatedSdkDevice or permissionType.permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }