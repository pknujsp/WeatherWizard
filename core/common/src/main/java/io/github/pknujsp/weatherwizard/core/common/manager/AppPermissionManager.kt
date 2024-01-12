package io.github.pknujsp.weatherwizard.core.common.manager

import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission_group.STORAGE
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


/**
 * @param onPermissionGranted 권한이 허용되었을 때 실행되는 람다
 * @param onPermissionDenied 권한이 거부되었을 때 실행되는 람다
 * @param onShouldShowRationale 권한이 필요한 이유를 설명해야 할 때 실행되는 람다
 * @param onNeverAskAgain 권한 요청이 다시 묻지 않음으로 되었을때 실행되는 람다
 */
@Composable
fun PermissionManager(
    permissionType: PermissionType,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    onShouldShowRationale: () -> Unit,
    onNeverAskAgain: () -> Unit,
    refreshKey: Int
) {
    val context = LocalContext.current
    val activity = context as Activity
    val permissionState by rememberUpdatedState(arrayOf(onPermissionGranted, onPermissionDenied, onShouldShowRationale, onNeverAskAgain))
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result.all { it.value }) {
            permissionState[0]()
        } else {
            if (activity.shouldShowRequestPermissionRationale(permissionType)) {
                permissionState[2]()
            } else {
                permissionState[1]()
            }
        }
    }

    LaunchedEffect(refreshKey) {
        permissionType.permissions.map { permission -> ContextCompat.checkSelfPermission(context, permission) }.let { result ->
            if (result.all { it == PackageManager.PERMISSION_GRANTED }) {
                permissionState[0]()
            } else {
                permissionLauncher.launch(permissionType.permissions)
            }
        }
    }
}


enum class PermissionType(val permissions: Array<String>, val isUnrelatedSdkDevice: Boolean) {
    LOCATION(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), isUnrelatedSdkDevice = false),
    BACKGROUND_LOCATION(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    } else {
        emptyArray()
    }, isUnrelatedSdkDevice = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q),
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
    }, isUnrelatedSdkDevice = false),

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    POST_NOTIFICATIONS(arrayOf(Manifest.permission.POST_NOTIFICATIONS),
        isUnrelatedSdkDevice = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU),

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
    }, isUnrelatedSdkDevice = Build.VERSION.SDK_INT < Build.VERSION_CODES.S);
}


private fun Activity.shouldShowRequestPermissionRationale(permissionType: PermissionType): Boolean =
    permissionType.isUnrelatedSdkDevice or permissionType.permissions.any { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }


/**
 * 해당 권한을 고려할 필요가 없는 SDK 버전이라면 즉시 true를 반환
 * @return 권한이 허용되었는지 여부
 */
fun Context.checkSelfPermission(permissionType: PermissionType): Boolean =
    permissionType.isUnrelatedSdkDevice or permissionType.permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }