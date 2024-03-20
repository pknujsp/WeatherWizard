package io.github.pknujsp.everyweather.feature.permoptimize.feature

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.feature.permoptimize.BaseFeatureStateManager

private class MutableFeatureStateManager(override val featureType: FeatureType) : FeatureStateManager()

@Composable
fun rememberFeatureStateManager(featureType: FeatureType): FeatureStateManager {
    val manager = remember(featureType) {
        MutableFeatureStateManager(featureType)
    }
    return manager
}

abstract class FeatureStateManager() : BaseFeatureStateManager()