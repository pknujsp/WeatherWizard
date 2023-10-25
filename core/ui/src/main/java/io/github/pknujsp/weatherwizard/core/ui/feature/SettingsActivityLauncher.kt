package io.github.pknujsp.weatherwizard.core.ui.feature

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.weatherwizard.core.common.FeatureType


@Composable
fun OpenAppSettingsActivity(featureType: FeatureType, onReturnedFromSettings: () -> Unit) {
    val context = LocalContext.current
    val onReturnedFromSettingsState by rememberUpdatedState(onReturnedFromSettings)

    val settingsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        onReturnedFromSettingsState()
    }

    LaunchedEffect(Unit) {
        val intent = Intent(featureType.settingsAction).apply {
            val uri = Uri.fromParts("package", context.packageName, null)
            data = uri
        }
        settingsLauncher.launch(intent)
    }
}

@Composable
fun OpenSettingsActivity(featureType: FeatureType, onReturnedFromSettings: () -> Unit) {
    val onReturnedFromSettingsState by rememberUpdatedState(onReturnedFromSettings)

    val settingsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        onReturnedFromSettingsState()
    }

    LaunchedEffect(Unit) {
        settingsLauncher.launch(Intent(featureType.settingsAction))
    }
}