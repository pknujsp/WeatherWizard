package io.github.pknujsp.weatherwizard.core.common.manager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.PowerManager
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.StatefulFeature
import io.github.pknujsp.weatherwizard.core.common.manager.StatefulFeatureStateManagerImpl.AvailableChecker
import io.github.pknujsp.weatherwizard.core.resource.R


sealed interface FeatureState {
    data object Available : FeatureState
    data class Unavailable(val featureType: FeatureType) : FeatureState
}

internal class StatefulFeatureStateManagerImpl(
    private val networkManager: AppNetworkManager, private val locationManager: AppLocationManager
) : FeatureStatusManager {

    @SuppressLint("NewApi") private val checkMethods = mapOf(
        FeatureType.NETWORK to AvailableChecker { _ ->
            networkManager.isNetworkAvailable()
        },
        FeatureType.LOCATION_SERVICE to AvailableChecker { _ ->
            locationManager.isGpsProviderEnabled
        },
        FeatureType.BATTERY_OPTIMIZATION to AvailableChecker { context ->
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S || (context.getSystemService(Context.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(
                context.packageName)
        },
        FeatureType.STORAGE_PERMISSION to AvailableChecker { context ->
            context.checkSelfPermission(PermissionType.STORAGE)
        },
        FeatureType.POST_NOTIFICATION_PERMISSION to AvailableChecker { context ->
            context.checkSelfPermission(PermissionType.POST_NOTIFICATIONS)
        },
        FeatureType.SCHEDULE_EXACT_ALARM_PERMISSION to AvailableChecker { context ->
            context.checkSelfPermission(PermissionType.SCHEDULE_EXACT_ALARM_ABOVE_EQUALS_ON_SDK_31)
        },
        FeatureType.LOCATION_PERMISSION to AvailableChecker { context ->
            context.checkSelfPermission(PermissionType.LOCATION)
        },
        FeatureType.BACKGROUND_LOCATION_PERMISSION to AvailableChecker { context ->
            context.checkSelfPermission(PermissionType.BACKGROUND_LOCATION)
        },
    )

    override fun status(context: Context, featureTypes: Array<FeatureType>): FeatureState {
        return featureTypes.firstOrNull {
            !checkMethods.getValue(it).isAvailable(context)
        }?.let {
            FeatureState.Unavailable(it)
        } ?: FeatureState.Available
    }

    private fun interface AvailableChecker {
        fun isAvailable(context: Context): Boolean
    }
}

interface FeatureStatusManager {
    fun status(context: Context, featureTypes: Array<FeatureType>): FeatureState
}

enum class FailedReason : StatefulFeature {
    SERVER_ERROR {
        override val title: Int = R.string.server_error_title
        override val message: Int = R.string.server_error_message
        override val action: Int = R.string.reload
        override val hasRepairAction: Boolean = true
    },
    UNKNOWN {
        override val title: Int = R.string.unknown_error_title
        override val message: Int = R.string.unknown_error_message
        override val action: Int = R.string.reload
        override val hasRepairAction: Boolean = true
    },
    REVERSE_GEOCODE_ERROR {
        override val title: Int = R.string.reverse_geocode_error_title
        override val message: Int = R.string.reverse_geocode_error_message
        override val action: Int = R.string.reload
        override val hasRepairAction: Boolean = true
    },
    CANCELED {
        override val title: Int = R.string.title_canceled_work
        override val message: Int = R.string.message_canceled_work
        override val action: Int = R.string.reload
        override val hasRepairAction: Boolean = true
    },
    BATTERY_OPTIMIZATION {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.battery_optimization
        override val message: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.battery_optimization_enabled
        override val action: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.open_settings_to_ignore_battery_optimization
        override val hasRepairAction: Boolean = true
    },
    LOCATION_SERVICE_DISABLED {
        override val title: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.location_service
        override val message: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.location_service_disabled
        override val action: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.open_settings_for_location_service
        override val hasRepairAction: Boolean = true
    },
    NETWORK_UNAVAILABLE {
        override val title: Int = R.string.network
        override val message: Int = R.string.network_unavailable
        override val action: Int = io.github.pknujsp.weatherwizard.core.resource.R.string.open_settings_for_network
        override val hasRepairAction: Boolean = true
    },
}