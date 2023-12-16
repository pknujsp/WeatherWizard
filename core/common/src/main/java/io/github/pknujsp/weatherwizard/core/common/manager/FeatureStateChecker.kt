package io.github.pknujsp.weatherwizard.core.common.manager

import android.content.Context
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.resource.R


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

enum class FailedReason(@StringRes val title: Int, @StringRes val message: Int, @StringRes val action: Int) {
    NETWORK_DISABLED(R.string.network, R.string.network_unavailable, R.string.open_settings_for_network),
    SERVER_ERROR(R.string.server_error_title, R.string.server_error_message, R.string.reload),
    UNKNOWN(R.string.unknown_error_title, R.string.unknown_error_message, R.string.reload),
    LOCATION_PROVIDER_DISABLED(R.string.location_service,
        R.string.location_service_disabled,
        R.string.open_settings_for_location_service),
    LOCATION_PERMISSION_DENIED(
        R.string.location_permission,
        R.string.location_permission_denied,
        R.string.open_settings_for_permission,
    ),
    STORAGE_PERMISSION_DENIED(
        R.string.storage_permission,
        R.string.storage_permission_denied,
        R.string.open_settings_for_permission,
    ),
    POST_NOTIFICATION_PERMISSION_DENIED(
        R.string.post_notification_permission,
        R.string.post_notification_permission_denied,
        R.string.open_settings_for_permission,
    ),
    EXACT_ALARM_PERMISSION_DENIED(
        R.string.exact_alarm_permission,
        R.string.exact_alarm_permission_denied,
        R.string.open_settings_for_permission,
    );
}