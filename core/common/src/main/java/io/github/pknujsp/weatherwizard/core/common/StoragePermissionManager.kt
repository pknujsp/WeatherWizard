package io.github.pknujsp.weatherwizard.core.common


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
fun StoragePermissionManager(
    onPermissionGranted: () -> Unit, onPermissionDenied: () -> Unit, onShouldShowRationale: () -> Unit,
    onNeverAskAgain: () -> Unit, refreshKey: Int
) {
    val context = LocalContext.current
    val activity = context as Activity
    val permissionState by rememberUpdatedState(
        arrayOf(onPermissionGranted, onPermissionDenied, onShouldShowRationale, onNeverAskAgain)
    )

    val permissions = remember {
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
    var requestPermission by remember { mutableStateOf(true) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.all { it.value }) {
            permissionState[0]()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
        permissions.map { permission -> ContextCompat.checkSelfPermission(context, permission) }.run {
            if (any { it != PackageManager.PERMISSION_GRANTED }) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    permissionState[2]()
                } else {
                    requestPermission = false
                    permissionLauncher.launch(permissions)
                }
            } else {
                permissionState[0]()
            }
        }
    }
}

@Composable
fun OpenSettingsForStoragePermission(onReturnedFromSettings: () -> Unit) {
    val context = LocalContext.current
    val onReturnedFromSettingsState by rememberUpdatedState(onReturnedFromSettings)

    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        onReturnedFromSettingsState()
    }

    LaunchedEffect(Unit) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            val uri = Uri.fromParts("package", context.packageName, null)
            data = uri
        }
        settingsLauncher.launch(intent)
    }
}