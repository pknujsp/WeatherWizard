package io.github.pknujsp.weatherwizard.core.ui.feature

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionState
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionType
import io.github.pknujsp.weatherwizard.core.common.manager.checkSelfPermission
import io.github.pknujsp.weatherwizard.core.common.manager.rememberPermissionManager

@Composable
fun PermissionStateScreen(navigate: () -> Unit, permissionType: PermissionType) {
    var openSettingsActivity by remember { mutableStateOf(false) }
    val permissionManager = rememberPermissionManager(defaultPermissionType = permissionType)

    Box {
        when (permissionManager.permissionState) {
            is PermissionState.Granted -> navigate()
            else ->{
                UnavailableFeatureScreen(featureType = featureType) {
                    openLocationPermissionActivity = true
                    locationPermissionGranted = context.checkSelfPermission(PermissionType.LOCATION)
                }
            }
        }
    }
}