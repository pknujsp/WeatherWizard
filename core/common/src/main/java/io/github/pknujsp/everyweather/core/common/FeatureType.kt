package io.github.pknujsp.everyweather.core.common

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.RequiresApi

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
            return PendingIntent.getActivity(context,
                this::class.simpleName.hashCode(),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context) = appSettingsIntent(context)

        override fun isAvailable(context: Context): Boolean {
            return context.checkSelfPermission(PermissionType.BACKGROUND_LOCATION)
        }
    },

    @SuppressLint("BatteryLife")
    BATTERY_OPTIMIZATION(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, FailedReason.BATTERY_OPTIMIZATION) {

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                this::class.simpleName.hashCode(),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context) = appSettingsIntent(context)

        override fun isAvailable(context: Context): Boolean {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || (context.getSystemService(Context.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(
                context.packageName)
        }

    },

    NETWORK(Settings.ACTION_WIRELESS_SETTINGS, FailedReason.NETWORK_UNAVAILABLE) {
        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                this::class.simpleName.hashCode(),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context) = actionIntent()

        override fun isAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return connectivityManager.activeNetwork != null
        }
    },
    LOCATION_SERVICE(Settings.ACTION_LOCATION_SOURCE_SETTINGS, FailedReason.LOCATION_SERVICE_DISABLED) {
        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                this::class.simpleName.hashCode(),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
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

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                this::class.simpleName.hashCode(),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context) = appSettingsIntent(context)

        override fun isAvailable(context: Context): Boolean {
            return context.checkSelfPermission(PermissionType.POST_NOTIFICATIONS)
        }
    },

    @RequiresApi(Build.VERSION_CODES.S)
    SCHEDULE_EXACT_ALARM_PERMISSION(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
        PermissionType.SCHEDULE_EXACT_ALARM_ABOVE_EQUALS_ON_SDK_31) {

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                this::class.simpleName.hashCode(),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context) = appSettingsIntent(context)

        override fun isAvailable(context: Context): Boolean {
            return context.checkSelfPermission(PermissionType.SCHEDULE_EXACT_ALARM_ABOVE_EQUALS_ON_SDK_31)
        }
    }, ;

    private companion object {
        fun FeatureType.appSettingsIntent(context: Context) = Intent(intentAction).apply {
            val uri = Uri.fromParts("package", context.packageName, null)
            data = uri
        }

        fun FeatureType.actionIntent() = Intent(intentAction)
    }
}


fun PermissionType.asFeatureType() = when (this) {
    PermissionType.LOCATION -> FeatureType.LOCATION_PERMISSION
    PermissionType.BACKGROUND_LOCATION -> FeatureType.BACKGROUND_LOCATION_PERMISSION
    PermissionType.POST_NOTIFICATIONS -> FeatureType.POST_NOTIFICATION_PERMISSION
    PermissionType.SCHEDULE_EXACT_ALARM_ABOVE_EQUALS_ON_SDK_31 -> FeatureType.SCHEDULE_EXACT_ALARM_PERMISSION
}


interface FeatureIntent {
    fun isAvailable(context: Context): Boolean
    fun getPendingIntent(context: Context): PendingIntent
    fun getIntent(context: Context): Intent
}

interface StatefulFeature {
    val title: Int
    val message: Int
    val action: Int
    val hasRepairAction: Boolean
    val hasRetryAction: Boolean
}