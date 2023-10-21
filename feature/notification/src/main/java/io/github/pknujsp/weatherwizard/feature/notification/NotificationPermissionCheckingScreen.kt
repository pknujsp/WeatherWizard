package io.github.pknujsp.weatherwizard.feature.notification

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.common.R
import io.github.pknujsp.weatherwizard.core.common.UnavailableFeature
import io.github.pknujsp.weatherwizard.core.common.permission.OpenSettingsActivityForPermission
import io.github.pknujsp.weatherwizard.core.common.permission.PermissionManager
import io.github.pknujsp.weatherwizard.core.common.permission.PermissionType
import io.github.pknujsp.weatherwizard.core.ui.UnavailableFeatureScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NotificationPermissionCheckingScreen(onPermissionGranted: () -> Unit) {
    var permissionGranted by remember { mutableStateOf(false) }
    var openPermissionActivity by remember { mutableStateOf(false) }

    var unavailable by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableIntStateOf(0) }

    if (permissionGranted) {
        onPermissionGranted()
    } else {
        PermissionManager(PermissionType.POST_NOTIFICATIONS, onPermissionGranted = {
            openPermissionActivity = false
            unavailable = false
            permissionGranted = true
        }, onPermissionDenied = {
            unavailable = true
        }, onShouldShowRationale = {
            unavailable = true
        }, onNeverAskAgain = {
            unavailable = true
        }, refreshKey)

        if (unavailable) {
            UnavailableFeatureScreen(title = R.string.title_why_you_need_permissions,
                unavailableFeature = UnavailableFeature.POST_NOTIFICATION_PERMISSION_DENIED) {
                openPermissionActivity = true
            }
        }
        if (openPermissionActivity) {
            OpenSettingsActivityForPermission {
                openPermissionActivity = false
                refreshKey++
            }
        }

    }
}