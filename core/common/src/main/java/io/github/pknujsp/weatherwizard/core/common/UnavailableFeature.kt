package io.github.pknujsp.weatherwizard.core.common

import androidx.annotation.StringRes

enum class UnavailableFeature(@StringRes val message: Int, @StringRes val action: Int) {
    LOCATION_PERMISSION_DENIED(R.string.location_permission_denied, R.string.open_settings_for_permission),
    STORAGE_PERMISSION_DENIED(R.string.storage_permission_denied, R.string.open_settings_for_permission),
    NETWORK_UNAVAILABLE(R.string.network_unavailable, R.string.open_settings_for_network),
    LOCATION_SERVICE_DISABLED(R.string.location_service_disabled, R.string.open_settings_for_location_service),
    POST_NOTIFICATION_PERMISSION_DENIED(R.string.post_notification_permission_denied, R.string
        .open_settings_for_permission),
}