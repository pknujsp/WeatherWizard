package io.github.pknujsp.weatherwizard.core.common.manager

import android.content.Context
import io.github.pknujsp.weatherwizard.core.common.FeatureType

fun Context.checkFeatureState(vararg featureTypes: FeatureType): List<FeatureType> {
    return featureTypes.firstOrNull {
        !it.isAvailable(this)
    }?.let {
        listOf(it)
    } ?: emptyList()
}