package io.github.pknujsp.weatherwizard.core.common.manager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.resource.R


sealed interface FeatureState {
    data object Available : FeatureState
    data class Unavailable(val featureType: FeatureType) : FeatureState
}

internal class FeatureStatusManagerImpl(
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


enum class FailedReason(@StringRes val title: Int, @StringRes val message: Int, @StringRes val action: Int) {
    NETWORK_DISABLED(R.string.network, R.string.network_unavailable, R.string.open_settings_for_network),
    SERVER_ERROR(R.string.server_error_title, R.string.server_error_message, R.string.reload),
    UNKNOWN(R.string.unknown_error_title, R.string.unknown_error_message, R.string.reload),
    LOCATION_PROVIDER_DISABLED(R.string.location_service, R.string.location_service_disabled, R.string.open_settings_for_location_service),
    ENABLED_BATTERY_OPTIMIZATION(
        R.string.battery_optimization,
        R.string.battery_optimization_enabled,
        R.string.open_settings_to_ignore_battery_optimization,
    ),
    LOCATION_PERMISSION_DENIED(
        R.string.location_permission,
        R.string.location_permission_denied,
        R.string.open_settings_for_permission,
    ),
    BACKGROUND_LOCATION_PERMISSION_DENIED(
        R.string.background_location_permission,
        R.string.background_location_permission_denied,
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