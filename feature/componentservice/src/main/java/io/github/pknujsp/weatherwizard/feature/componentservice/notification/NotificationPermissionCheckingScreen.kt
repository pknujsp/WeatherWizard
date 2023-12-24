package io.github.pknujsp.weatherwizard.feature.componentservice.notification

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionManager
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionType
import io.github.pknujsp.weatherwizard.core.common.manager.checkSelfPermission
import io.github.pknujsp.weatherwizard.core.ui.feature.UnavailableFeatureScreen
import io.github.pknujsp.weatherwizard.core.ui.feature.OpenAppSettingsActivity

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NotificationPermissionCheckingScreen(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(context.checkSelfPermission(PermissionType.POST_NOTIFICATIONS)) }
    var openPermissionActivity by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableIntStateOf(0) }

    if (permissionGranted) {
        onPermissionGranted()
    } else {
        PermissionManager(PermissionType.POST_NOTIFICATIONS, onPermissionGranted = {
            openPermissionActivity = false
            permissionGranted = true
        }, onPermissionDenied = {
            permissionGranted = false
        }, onShouldShowRationale = {
            permissionGranted = false
        }, onNeverAskAgain = {
            permissionGranted = false
        }, refreshKey)

        UnavailableFeatureScreen(featureType = FeatureType.POST_NOTIFICATION_PERMISSION) {
            openPermissionActivity = true
        }
        if (openPermissionActivity) {
            OpenAppSettingsActivity(FeatureType.POST_NOTIFICATION_PERMISSION) {
                openPermissionActivity = false
                refreshKey++
            }
        }
    }
}