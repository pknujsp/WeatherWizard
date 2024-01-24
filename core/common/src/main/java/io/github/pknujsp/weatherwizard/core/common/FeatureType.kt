package io.github.pknujsp.weatherwizard.core.common

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionType
import io.github.pknujsp.weatherwizard.core.common.manager.checkSelfPermission

enum class FeatureType(
    protected val intentAction: String,
    protected val statefulFeature: StatefulFeature,
) : FeatureIntent, StatefulFeature by statefulFeature {
    LOCATION_PERMISSION(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, PermissionType.LOCATION) {

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                this::class.simpleName.hashCode(),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context) = appSettingsIntent(context)

        override fun isAvailable(context: Context): Boolean {
            return context.checkSelfPermission(PermissionType.LOCATION)
        }
    },
    BACKGROUND_LOCATION_PERMISSION(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, PermissionType.BACKGROUND_LOCATION) {

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context, this::class.simpleName.hashCode(),

                getIntent(context), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context) = appSettingsIntent(context)

        override fun isAvailable(context: Context): Boolean {
            return context.checkSelfPermission(PermissionType.BACKGROUND_LOCATION)
        }
    },
    BATTERY_OPTIMIZATION(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS, FailedReason.BATTERY_OPTIMIZATION) {

        @RequiresApi(Build.VERSION_CODES.S)
        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context, this::class.simpleName.hashCode(),

                getIntent(context), PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        @RequiresApi(Build.VERSION_CODES.S)
        override fun getIntent(context: Context) = actionIntent(context).apply {
            data = Uri.parse("package:${context.packageName}")
        }

        @RequiresApi(Build.VERSION_CODES.S)
        override fun isAvailable(context: Context): Boolean =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S || (context.getSystemService(Context.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(
                context.packageName)
    },
    STORAGE_PERMISSION(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, PermissionType.STORAGE) {
        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context, this::class.simpleName.hashCode(),

                getIntent(context), PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context) = appSettingsIntent(context)

        override fun isAvailable(context: Context): Boolean {
            return context.checkSelfPermission(PermissionType.STORAGE)
        }
    },
    NETWORK(Settings.ACTION_WIRELESS_SETTINGS, FailedReason.NETWORK_UNAVAILABLE) {
        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context, this::class.simpleName.hashCode(),

                getIntent(context), PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context) = actionIntent(context)

        override fun isAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } ?: false
        }
    },
    LOCATION_SERVICE(Settings.ACTION_LOCATION_SOURCE_SETTINGS, FailedReason.LOCATION_SERVICE_DISABLED) {
        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context, this::class.simpleName.hashCode(),

                getIntent(context), PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context): Intent {
            return Intent(intentAction)
        }

        override fun isAvailable(context: Context): Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }
    },
    POST_NOTIFICATION_PERMISSION(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, PermissionType.POST_NOTIFICATIONS) {

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context, this::class.simpleName.hashCode(),

                getIntent(context), PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun getIntent(context: Context) = appSettingsIntent(context)

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun isAvailable(context: Context): Boolean {
            return context.checkSelfPermission(PermissionType.POST_NOTIFICATIONS)
        }
    },
    SCHEDULE_EXACT_ALARM_PERMISSION(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        PermissionType.SCHEDULE_EXACT_ALARM_ABOVE_EQUALS_ON_SDK_31) {


        @RequiresApi(Build.VERSION_CODES.S)
        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                this::class.simpleName.hashCode(),
                getIntent(context),
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        @RequiresApi(Build.VERSION_CODES.S)
        override fun getIntent(context: Context) = appSettingsIntent(context)

        @RequiresApi(Build.VERSION_CODES.S)
        override fun isAvailable(context: Context): Boolean {
            return context.checkSelfPermission(PermissionType.SCHEDULE_EXACT_ALARM_ABOVE_EQUALS_ON_SDK_31)
        }
    }, ;

    private companion object {
        fun FeatureType.appSettingsIntent(context: Context) = Intent(intentAction).apply {
            val uri = Uri.fromParts("package", context.packageName, null)
            data = uri
        }

        fun FeatureType.actionIntent(context: Context) = Intent(intentAction).apply {
            val uri = Uri.fromParts("package", context.packageName, null)
            data = uri
        }
    }
}

fun PermissionType.asFeatureType() = when (this) {
    PermissionType.LOCATION -> FeatureType.LOCATION_PERMISSION
    PermissionType.BACKGROUND_LOCATION -> FeatureType.BACKGROUND_LOCATION_PERMISSION
    PermissionType.STORAGE -> FeatureType.STORAGE_PERMISSION
    PermissionType.POST_NOTIFICATIONS -> FeatureType.POST_NOTIFICATION_PERMISSION
    PermissionType.SCHEDULE_EXACT_ALARM_ABOVE_EQUALS_ON_SDK_31 -> FeatureType.SCHEDULE_EXACT_ALARM_PERMISSION
}


interface FeatureIntent {
    fun getPendingIntent(context: Context): PendingIntent
    fun getIntent(context: Context): Intent
    fun isAvailable(context: Context): Boolean
}

interface StatefulFeature {
    val title: Int
    val message: Int
    val action: Int
    val hasRepairAction: Boolean
}