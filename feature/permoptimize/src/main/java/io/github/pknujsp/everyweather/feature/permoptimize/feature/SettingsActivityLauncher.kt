package io.github.pknujsp.everyweather.feature.permoptimize.feature

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.core.common.FeatureType

@Composable
fun ShowSettingsActivity(
        featureType: FeatureType,
        onReturnedFromSettings: () -> Unit,
) {
    val context = LocalContext.current
    val onReturnedFromSettings by rememberUpdatedState(onReturnedFromSettings)

    val settingsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        onReturnedFromSettings()
    }

    LaunchedEffect(Unit) {
        settingsLauncher.launch(featureType.getIntent(context))
    }
}