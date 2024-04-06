package io.github.pknujsp.everyweather.feature.permoptimize.feature

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.feature.permoptimize.BaseFeatureStateManager

@Stable
private class FeatureStateManagerImpl(override val featureType: FeatureType) : FeatureStateManager()

@Composable
fun rememberFeatureStateManager(featureType: FeatureType): FeatureStateManager {
    val manager = remember {
        FeatureStateManagerImpl(featureType)
    }
    return manager
}

@Stable
abstract class FeatureStateManager() : BaseFeatureStateManager()