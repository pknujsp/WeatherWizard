package io.github.pknujsp.weatherwizard.core.common

import androidx.annotation.StringRes

enum class FeatureType(@StringRes val title: Int, @StringRes val alertMessage: Int, @StringRes val action: Int) {
    LOCATION_PERMISSION(R.string.location_permission, R.string.location_permission_denied, R.string.open_settings_for_permission),
    STORAGE_PERMISSION(R.string.storage_permission, R.string.storage_permission_denied, R.string.open_settings_for_permission),
    NETWORK(R.string.network, R.string.network_unavailable, R.string.open_settings_for_network),
    LOCATION_SERVICE(R.string.location_service, R.string.location_service_disabled, R.string.open_settings_for_location_service),
    POST_NOTIFICATION_PERMISSION(R.string.post_notification_permission,
        R.string.post_notification_permission_denied,
        R.string.open_settings_for_permission),
    EXACT_ALARM_PERMISSION(R.string.exact_alarm_permission, R.string.exact_alarm_permission_denied, R.string.open_settings_for_permission),
}