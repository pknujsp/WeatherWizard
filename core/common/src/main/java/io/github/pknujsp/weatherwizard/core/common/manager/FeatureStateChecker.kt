package io.github.pknujsp.weatherwizard.core.common.manager

import android.content.Context
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.R


sealed interface FeatureState {
    data object Available : FeatureState
    data class Unavailable(val featureType: FeatureType) : FeatureState
}

object FeatureStateChecker {
    fun checkFeatureState(context: Context, featureTypes: Array<FeatureType>): FeatureState {
        return featureTypes.firstOrNull {
            !it.isAvailable(context)
        }?.let {
            FeatureState.Unavailable(it)
        } ?: FeatureState.Available
    }
}

enum class FailedReason(@StringRes val title: Int, @StringRes val message: Int) {
    NETWORK_DISABLED(R.string.title_network_is_unavailable, R.string.network_unavailable),
    SERVER_ERROR(R.string.server_error_title, R.string.server_error_message),
    UNKNOWN(R.string.unknown_error_title, R.string.unknown_error_message),
    LOCATION_PROVIDER_DISABLED(R.string.title_location_is_disabled, R.string.location_service_disabled),
}