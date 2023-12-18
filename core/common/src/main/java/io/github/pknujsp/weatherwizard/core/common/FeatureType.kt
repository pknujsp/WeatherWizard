package io.github.pknujsp.weatherwizard.core.common

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import io.github.pknujsp.weatherwizard.core.common.enum.pendingIntentRequestFactory
import io.github.pknujsp.weatherwizard.core.common.manager.AppNetworkManager
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionType
import io.github.pknujsp.weatherwizard.core.common.manager.checkSelfPermission

enum class FeatureType(
    val failedReason: FailedReason, protected val intentAction: String
) : IFeature {
    LOCATION_PERMISSION(FailedReason.LOCATION_PERMISSION_DENIED, Settings.ACTION_APPLICATION_DETAILS_SETTINGS) {

        override fun isAvailable(context: Context): Boolean {
            return context.checkSelfPermission(PermissionType.LOCATION)
        }

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context): Intent {
            return Intent(intentAction).apply {
                val uri = Uri.fromParts("package", context.packageName, null)
                data = uri
            }
        }
    },
    BATTERY_OPTIMIZATION(FailedReason.ENABLED_BATTERY_OPTIMIZATION, Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS) {

        @RequiresApi(Build.VERSION_CODES.S)
        override fun isAvailable(context: Context): Boolean {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || (context.getSystemService(Context.POWER_SERVICE) as PowerManager).isIgnoringBatteryOptimizations(
                context.packageName)
        }

        @RequiresApi(Build.VERSION_CODES.S)
        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        @RequiresApi(Build.VERSION_CODES.S)
        override fun getIntent(context: Context): Intent {
            return Intent(intentAction)
        }
    },
    STORAGE_PERMISSION(FailedReason.STORAGE_PERMISSION_DENIED, Settings.ACTION_APPLICATION_DETAILS_SETTINGS) {

        override fun isAvailable(context: Context): Boolean {
            return context.checkSelfPermission(PermissionType.STORAGE)
        }

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context): Intent {
            return Intent(intentAction).apply {
                val uri = Uri.fromParts("package", context.packageName, null)
                data = uri
            }
        }
    },
    NETWORK(FailedReason.NETWORK_DISABLED, Settings.ACTION_WIRELESS_SETTINGS) {

        override fun isAvailable(context: Context): Boolean {
            return AppNetworkManager.getInstance(context).isNetworkAvailable()
        }

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context): Intent {
            return Intent(intentAction)
        }
    },
    LOCATION_SERVICE(FailedReason.LOCATION_PROVIDER_DISABLED, Settings.ACTION_LOCATION_SOURCE_SETTINGS) {

        override fun isAvailable(context: Context): Boolean =
            (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)


        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context): Intent {
            return Intent(intentAction)
        }
    },
    POST_NOTIFICATION_PERMISSION(FailedReason.POST_NOTIFICATION_PERMISSION_DENIED, Settings.ACTION_APPLICATION_DETAILS_SETTINGS) {


        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun isAvailable(context: Context): Boolean {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || context.checkSelfPermission(PermissionType.POST_NOTIFICATIONS)
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun getIntent(context: Context): Intent {
            return Intent(intentAction).apply {
                val uri = Uri.fromParts("package", context.packageName, null)
                data = uri
            }
        }
    },
    SCHEDULE_EXACT_ALARM_PERMISSION(FailedReason.EXACT_ALARM_PERMISSION_DENIED, Settings.ACTION_APPLICATION_DETAILS_SETTINGS) {

        @RequiresApi(Build.VERSION_CODES.S)
        override fun isAvailable(context: Context): Boolean =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S || Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2 || context.checkSelfPermission(
                PermissionType.SCHEDULE_EXACT_ALARM_ON_SDK_31_AND_32)


        @RequiresApi(Build.VERSION_CODES.S)
        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        @RequiresApi(Build.VERSION_CODES.S)
        override fun getIntent(context: Context): Intent {
            return Intent(intentAction).apply {
                val uri = Uri.fromParts("package", context.packageName, null)
                data = uri
            }
        }
    },

}


interface IFeature {
    fun isAvailable(context: Context): Boolean
    fun getPendingIntent(context: Context): PendingIntent
    fun getIntent(context: Context): Intent
}