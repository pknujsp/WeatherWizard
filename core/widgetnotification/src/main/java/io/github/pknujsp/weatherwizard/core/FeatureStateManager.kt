package io.github.pknujsp.weatherwizard.core

import android.content.Context
import io.github.pknujsp.weatherwizard.core.common.FeatureType

class FeatureStateManagerImpl : FeatureStateManager {
    override fun retrieveFeaturesState(featureTypes: Array<FeatureType>, context: Context): FeatureStateManager.FeatureState {
        return featureTypes.firstOrNull {
            !it.isAvailable(context)
        }?.let {
            FeatureStateManager.FeatureState.Unavailable(it)
        } ?: FeatureStateManager.FeatureState.Available
    }

}

interface FeatureStateManager {
    fun retrieveFeaturesState(featureTypes: Array<FeatureType>, context: Context): FeatureState

    sealed interface FeatureState {
        data class Unavailable(val featureType: FeatureType) : FeatureState
        data object Available : FeatureState
    }
}