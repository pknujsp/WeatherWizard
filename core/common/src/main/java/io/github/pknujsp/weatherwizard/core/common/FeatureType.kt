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
import io.github.pknujsp.weatherwizard.core.common.manager.PermissionType

enum class FeatureType(
    protected val intentAction: String,
    val statefulFeature: StatefulFeature,
) : FeatureIntent {
    LOCATION_PERMISSION(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, PermissionType.LOCATION) {

        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context) = appSettingsIntent(context)
    },
    BACKGROUND_LOCATION_PERMISSION(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, PermissionType.BACKGROUND_LOCATION) {


        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context) = appSettingsIntent(context)
    },
    BATTERY_OPTIMIZATION(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS, FailedReason.BATTERY_OPTIMIZATION) {


        @RequiresApi(Build.VERSION_CODES.S)
        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        @RequiresApi(Build.VERSION_CODES.S)
        override fun getIntent(context: Context) = actionIntent(context).apply {
            data = Uri.parse("package:${context.packageName}")
        }
    },
    STORAGE_PERMISSION(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, PermissionType.STORAGE) {


        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context) = appSettingsIntent(context)
    },
    NETWORK(Settings.ACTION_WIRELESS_SETTINGS, FailedReason.NETWORK_UNAVAILABLE) {


        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        override fun getIntent(context: Context) = actionIntent(context)
    },
    LOCATION_SERVICE(Settings.ACTION_LOCATION_SOURCE_SETTINGS, FailedReason.LOCATION_SERVICE_DISABLED) {


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
    POST_NOTIFICATION_PERMISSION(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, PermissionType.POST_NOTIFICATIONS) {


        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun getIntent(context: Context) = appSettingsIntent(context)
    },
    SCHEDULE_EXACT_ALARM_PERMISSION(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        PermissionType.SCHEDULE_EXACT_ALARM_ABOVE_EQUALS_ON_SDK_31) {


        @RequiresApi(Build.VERSION_CODES.S)
        override fun getPendingIntent(context: Context): PendingIntent {
            return PendingIntent.getActivity(context,
                pendingIntentRequestFactory.requestId(this::class),
                getIntent(context),
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        }

        @RequiresApi(Build.VERSION_CODES.S)
        override fun getIntent(context: Context) = appSettingsIntent(context)
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


interface FeatureIntent {
    fun getPendingIntent(context: Context): PendingIntent
    fun getIntent(context: Context): Intent
}

interface StatefulFeature {
    val title: Int
    val message: Int
    val action: Int
    val hasRepairAction: Boolean
}