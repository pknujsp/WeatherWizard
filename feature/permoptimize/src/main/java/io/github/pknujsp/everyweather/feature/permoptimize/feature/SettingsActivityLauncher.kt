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
fun ShowAppSettingsActivity(
    featureType: FeatureType,
    onReturnedFromSettings: () -> Unit,
) {
    val context = LocalContext.current
    val onReturnedFromSettingsState by rememberUpdatedState(onReturnedFromSettings)

    val settingsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onReturnedFromSettingsState()
        }

    LaunchedEffect(Unit) {
        settingsLauncher.launch(featureType.getIntent(context))
    }
}
