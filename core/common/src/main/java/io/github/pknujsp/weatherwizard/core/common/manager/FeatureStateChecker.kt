package io.github.pknujsp.weatherwizard.core.common.manager

import android.content.Context
import io.github.pknujsp.weatherwizard.core.common.FeatureType

fun Context.checkFeatureStateAndUpdateWidgets(featureTypes: Array<FeatureType>): FeatureState {
    return featureTypes.firstOrNull {
        !it.isAvailable(this)
    }?.let {
        FeatureState.Unavailable(it)
    } ?: FeatureState.Available
}

sealed interface FeatureState {
    data object Available : FeatureState
    data class Unavailable(val featureType: FeatureType) : FeatureState
}