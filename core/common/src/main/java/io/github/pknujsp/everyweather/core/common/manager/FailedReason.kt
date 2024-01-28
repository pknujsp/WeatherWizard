package io.github.pknujsp.everyweather.core.common.manager

import io.github.pknujsp.everyweather.core.common.StatefulFeature
import io.github.pknujsp.everyweather.core.resource.R

enum class FailedReason : StatefulFeature {
    SERVER_ERROR {
        override val title: Int = R.string.server_error_title
        override val message: Int = R.string.server_error_message
        override val action: Int = R.string.reload
        override val hasRepairAction: Boolean = false
    },
    UNKNOWN {
        override val title: Int = R.string.unknown_error_title
        override val message: Int = R.string.unknown_error_message
        override val action: Int = R.string.reload
        override val hasRepairAction: Boolean = false
    },
    REVERSE_GEOCODE_ERROR {
        override val title: Int = R.string.reverse_geocode_error_title
        override val message: Int = R.string.reverse_geocode_error_message
        override val action: Int = R.string.reload
        override val hasRepairAction: Boolean = false
    },
    CANCELED {
        override val title: Int = R.string.title_canceled_work
        override val message: Int = R.string.message_canceled_work
        override val action: Int = R.string.reload
        override val hasRepairAction: Boolean = false
    },
    BATTERY_OPTIMIZATION {
        override val title: Int = io.github.pknujsp.everyweather.core.resource.R.string.battery_optimization
        override val message: Int = io.github.pknujsp.everyweather.core.resource.R.string.battery_optimization_enabled
        override val action: Int = io.github.pknujsp.everyweather.core.resource.R.string.open_settings_to_ignore_battery_optimization
        override val hasRepairAction: Boolean = true
    },
    LOCATION_SERVICE_DISABLED {
        override val title: Int = io.github.pknujsp.everyweather.core.resource.R.string.location_service
        override val message: Int = io.github.pknujsp.everyweather.core.resource.R.string.location_service_disabled
        override val action: Int = io.github.pknujsp.everyweather.core.resource.R.string.open_settings_for_location_service
        override val hasRepairAction: Boolean = true
    },
    NETWORK_UNAVAILABLE {
        override val title: Int = R.string.network
        override val message: Int = R.string.network_unavailable
        override val action: Int = io.github.pknujsp.everyweather.core.resource.R.string.open_settings_for_network
        override val hasRepairAction: Boolean = true
    },
}