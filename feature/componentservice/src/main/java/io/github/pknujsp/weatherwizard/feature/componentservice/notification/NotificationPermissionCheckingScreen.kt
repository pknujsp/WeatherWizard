package io.github.pknujsp.weatherwizard.feature.componentservice.notification

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionManager
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionType
import io.github.pknujsp.weatherwizard.core.ui.feature.UnavailableFeatureScreen
import io.github.pknujsp.weatherwizard.core.ui.feature.OpenAppSettingsActivity

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
            UnavailableFeatureScreen(
                featureType = FeatureType.POST_NOTIFICATION_PERMISSION) {
                openPermissionActivity = true
            }
        }
        if (openPermissionActivity) {
            OpenAppSettingsActivity(FeatureType.POST_NOTIFICATION_PERMISSION) {
                openPermissionActivity = false
                refreshKey++
            }
        }

    }
}