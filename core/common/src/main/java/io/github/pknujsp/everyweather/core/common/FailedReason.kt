package io.github.pknujsp.everyweather.core.common

import io.github.pknujsp.everyweather.core.resource.R

enum class FailedReason : StatefulFeature {
    SERVER_ERROR {
        override val title: Int = R.string.server_error_title
        override val message: Int = R.string.server_error_message
        override val action: Int = R.string.reload
        override val hasRetryAction: Boolean = true
        override val hasRepairAction: Boolean = false
    },
    UNKNOWN {
        override val title: Int = R.string.unknown_error_title
        override val message: Int = R.string.unknown_error_message
        override val action: Int = R.string.reload
        override val hasRetryAction: Boolean = true
        override val hasRepairAction: Boolean = false
    },
    REVERSE_GEOCODE_ERROR {
        override val title: Int = R.string.reverse_geocode_error_title
        override val message: Int = R.string.reverse_geocode_error_message
        override val action: Int = R.string.reload
        override val hasRetryAction: Boolean = true
        override val hasRepairAction: Boolean = false
    },
    CANCELED {
        override val title: Int = R.string.title_canceled_work
        override val message: Int = R.string.message_canceled_work
        override val action: Int = R.string.reload
        override val hasRetryAction: Boolean = true
        override val hasRepairAction: Boolean = false
    },
    FAILED_TO_GET_LOCATION {
        override val title: Int = R.string.failed_to_get_location_error_title
        override val message: Int = R.string.failed_to_get_location_error_message
        override val action: Int = R.string.reload
        override val hasRetryAction: Boolean = true
        override val hasRepairAction: Boolean = false
    },
}
