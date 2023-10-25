package io.github.pknujsp.weatherwizard.feature.alarm

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import io.github.pknujsp.weatherwizard.core.common.R
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.permission.OpenSettingsActivityForPermission
import io.github.pknujsp.weatherwizard.core.common.permission.PermissionManager
import io.github.pknujsp.weatherwizard.core.common.permission.PermissionType
import io.github.pknujsp.weatherwizard.core.ui.UnavailableFeatureScreen

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AlarmPermissionCheckingScreen(navController: NavController, onPermissionGranted: () -> Unit) {
    val permissionType = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) PermissionType.EXACT_ALARM_ON_SDK_33_AND_ABOVE else PermissionType
            .EXACT_ALARM_ON_SDK_31_AND_ABOVE
    }
    var permissionGranted by remember { mutableStateOf(false) }
    var openPermissionActivity by remember { mutableStateOf(false) }

    var unavailable by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableIntStateOf(0) }

    if (permissionGranted) {
        onPermissionGranted()
    } else {
        if (unavailable) {
            UnavailableFeatureScreen(title = R.string.title_why_you_need_permissions,
                featureType = FeatureType.EXACT_ALARM_PERMISSION) {
                openPermissionActivity = true
            }
        }
        if (openPermissionActivity) {
            OpenSettingsActivityForPermission {
                openPermissionActivity = false
                refreshKey++
            }
        }
        PermissionManager(permissionType, onPermissionGranted = {
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
    }
}