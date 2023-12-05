package io.github.pknujsp.weatherwizard.core.common

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import io.github.pknujsp.weatherwizard.core.common.manager.AppNetworkManager
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionType
import io.github.pknujsp.weatherwizard.core.common.manager.checkSelfPermission

enum class FeatureType(
    @StringRes val title: Int, @StringRes val alertMessage: Int, @StringRes val actionMessage: Int, val settingsAction: String
) : IFeature {
    LOCATION_PERMISSION(R.string.location_permission,
        R.string.location_permission_denied,
        R.string.open_settings_for_permission,
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS) {
        override fun isAvailable(context: Context): Boolean {
            return context.checkSelfPermission(PermissionType.LOCATION)
        }

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                title,
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context): Intent {
            return Intent(settingsAction).apply {
                val uri = Uri.fromParts("package", context.packageName, null)
                data = uri
            }
        }
    },
    STORAGE_PERMISSION(R.string.storage_permission,
        R.string.storage_permission_denied,
        R.string.open_settings_for_permission,
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS) {
        override fun isAvailable(context: Context): Boolean {
            return context.checkSelfPermission(PermissionType.STORAGE)
        }

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                title,
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context): Intent {
            return Intent(settingsAction).apply {
                val uri = Uri.fromParts("package", context.packageName, null)
                data = uri
            }
        }
    },
    NETWORK(R.string.network, R.string.network_unavailable, R.string.open_settings_for_network, Settings.ACTION_WIRELESS_SETTINGS) {
        override fun isAvailable(context: Context): Boolean {
            return AppNetworkManager.getInstance(context).isNetworkAvailable()
        }

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                title,
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context): Intent {
            return Intent(settingsAction)
        }
    },
    LOCATION_SERVICE(R.string.location_service,
        R.string.location_service_disabled,
        R.string.open_settings_for_location_service,
        Settings.ACTION_LOCATION_SOURCE_SETTINGS) {
        override fun isAvailable(context: Context): Boolean =
            (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)


        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                title,
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context): Intent {
            return Intent(settingsAction)
        }
    },
    POST_NOTIFICATION_PERMISSION(R.string.post_notification_permission,
        R.string.post_notification_permission_denied,
        R.string.open_settings_for_permission,
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS) {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun isAvailable(context: Context): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                return true
            }
            return context.checkSelfPermission(PermissionType.POST_NOTIFICATIONS)
        }

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                title,
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context): Intent {
            return Intent(settingsAction).apply {
                val uri = Uri.fromParts("package", context.packageName, null)
                data = uri
            }
        }
    },
    EXACT_ALARM_PERMISSION(R.string.exact_alarm_permission,
        R.string.exact_alarm_permission_denied,
        R.string.open_settings_for_permission,
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS) {
        @RequiresApi(Build.VERSION_CODES.S)
        override fun isAvailable(context: Context): Boolean {
            return context.checkSelfPermission(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) PermissionType.EXACT_ALARM_ON_SDK_33_AND_ABOVE else PermissionType.EXACT_ALARM_ON_SDK_31_AND_ABOVE)
        }

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                title,
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context): Intent {
            return Intent(settingsAction).apply {
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