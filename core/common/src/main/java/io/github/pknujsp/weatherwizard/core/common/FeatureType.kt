package io.github.pknujsp.weatherwizard.core.common

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import io.github.pknujsp.weatherwizard.core.common.enum.pendingIntentRequestFactory
import io.github.pknujsp.weatherwizard.core.common.manager.FailedReason

enum class FeatureType(
    val failedReason: FailedReason, protected val intentAction: String
) : FeatureIntent {
    LOCATION_PERMISSION(FailedReason.LOCATION_PERMISSION_DENIED, Settings.ACTION_APPLICATION_DETAILS_SETTINGS) {

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context): Intent {
            return Intent(intentAction).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        }
    },
    BACKGROUND_LOCATION_PERMISSION(FailedReason.BACKGROUND_LOCATION_PERMISSION_DENIED, Settings.ACTION_APPLICATION_DETAILS_SETTINGS) {

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context): Intent {
            return Intent(intentAction).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        }
    },
    BATTERY_OPTIMIZATION(FailedReason.ENABLED_BATTERY_OPTIMIZATION, Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS) {

        @RequiresApi(Build.VERSION_CODES.S)
        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        @RequiresApi(Build.VERSION_CODES.S)
        override fun getIntent(context: Context): Intent {
            return Intent(intentAction).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        }
    },
    STORAGE_PERMISSION(FailedReason.STORAGE_PERMISSION_DENIED, Settings.ACTION_APPLICATION_DETAILS_SETTINGS) {


        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context): Intent {
            return Intent(intentAction).apply {
                val uri = Uri.fromParts("package", context.packageName, null)
                data = uri
            }
        }
    },
    NETWORK(FailedReason.NETWORK_DISABLED, Settings.ACTION_WIRELESS_SETTINGS) {

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context): Intent {
            return Intent(intentAction)
        }
    },
    LOCATION_SERVICE(FailedReason.LOCATION_PROVIDER_DISABLED, Settings.ACTION_LOCATION_SOURCE_SETTINGS) {

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context): Intent {
            return Intent(intentAction)
        }
    },
    POST_NOTIFICATION_PERMISSION(FailedReason.POST_NOTIFICATION_PERMISSION_DENIED, Settings.ACTION_APPLICATION_DETAILS_SETTINGS) {


        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
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
        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
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


interface FeatureIntent {
    fun getPendingIntent(context: Context): PendingIntent
    fun getIntent(context: Context): Intent
}