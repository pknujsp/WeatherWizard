package io.github.pknujsp.weatherwizard.core.ui.feature

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.weatherwizard.core.common.FeatureType

@Composable
fun FeatureStateScreen(featureType: FeatureType, onAvailable: () -> Unit) {
    val context = LocalContext.current
    val availableState by rememberUpdatedState(newValue = onAvailable)
    var openSettingsActivity by remember { mutableStateOf(false) }
    val isAvailable by remember {
        derivedStateOf {
            if (openSettingsActivity) false
            else featureType.isAvailable(context)
        }
    }

    Box {
        when (isAvailable) {
            true -> availableState()
            else -> {
                UnavailableFeatureScreen(featureType = featureType) {
                    openSettingsActivity = true
                }
                if (openSettingsActivity) {
                    OpenAppSettingsActivity(featureType) {
                        openSettingsActivity = false
                    }
                }
            }
        }
    }
}