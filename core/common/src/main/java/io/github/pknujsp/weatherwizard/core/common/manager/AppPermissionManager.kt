package io.github.pknujsp.weatherwizard.core.common.manager

import android.Manifest
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
import dagger.hilt.android.qualifiers.ApplicationContext


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

    var requestPermission by remember { mutableStateOf(true) }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result.all { it.value }) {
            permissionState[0]()
        } else {
            if (activity.shouldShowRequestPermissionRationale(permissionType)) {
                permissionState[2]()
            } else {
                if (requestPermission) {
                    permissionState[3]()
                } else {
                    permissionState[1]()
                }
            }
        }
    }

    LaunchedEffect(refreshKey) {
        permissionType.permissions.map { permission -> ContextCompat.checkSelfPermission(context, permission) }.run {
            if (any { it != PackageManager.PERMISSION_GRANTED }) {
                if (activity.shouldShowRequestPermissionRationale(permissionType)) {
                    permissionState[2]()
                } else {
                    requestPermission = false
                    permissionLauncher.launch(permissionType.permissions)
                }
            } else {
                permissionState[0]()
            }
        }
    }
}


enum class PermissionType(val permissions: Array<String>) {
    LOCATION(
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                it + Manifest.permission.ACCESS_BACKGROUND_LOCATION
            } else {
                it
            }
        },
    ),
    STORAGE(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
        },
    ),

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    POST_NOTIFICATIONS(arrayOf(Manifest.permission.POST_NOTIFICATIONS)),

    @RequiresApi(Build.VERSION_CODES.S)
    EXACT_ALARM_ON_SDK_31_AND_ABOVE(
        arrayOf(
            Manifest.permission.SCHEDULE_EXACT_ALARM,
        ),
    ),

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    EXACT_ALARM_ON_SDK_33_AND_ABOVE(
        arrayOf(
            Manifest.permission.USE_EXACT_ALARM,
        ),
    );
}

fun Activity.shouldShowRequestPermissionRationale(permissionType: PermissionType): Boolean =
    permissionType.permissions.all { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }


fun Context.checkSelfPermission(permissionType: PermissionType): Boolean =
    permissionType.permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }