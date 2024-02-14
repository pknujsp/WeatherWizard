package io.github.pknujsp.everyweather.core.common

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.github.pknujsp.everyweather.core.resource.R

sealed interface FeatureType : FeatureIntent, StatefulFeature {
    val intentAction: String

    sealed interface Permission : FeatureType {
        val permissions: Array<String>
        val isUnrelatedSdkDevice: Boolean

        data object Location : Permission {
            override val intentAction: String = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            override val permissions: Array<String> =
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            override val title: Int = io.github.pknujsp.everyweather.core.resource.R.string.location_permission
            override val message: Int = io.github.pknujsp.everyweather.core.resource.R.string.location_permission_denied
            override val action: Int = io.github.pknujsp.everyweather.core.resource.R.string.grant_permissions
            override val hasRepairAction: Boolean = true
            override val hasRetryAction: Boolean = true
            override val reason: Int = io.github.pknujsp.everyweather.core.resource.R.string.location_permission_description
            override val isUnrelatedSdkDevice: Boolean = false

            override fun getPendingIntent(context: Context): PendingIntent {
                return PendingIntent.getActivity(context,
                    this::class.simpleName.hashCode(),
                    getIntent(context),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }

            override fun getIntent(context: Context) = appSettingsIntent(context)

            override fun isAvailable(context: Context): Boolean {
                return context.checkSelfPermission(this)
            }
        }

        data object ForegroundLocation : Permission {
            override val intentAction: String = Settings.ACTION_APPLICATION_DETAILS_SETTINGS

            @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE) override val permissions: Array<String> =
                arrayOf(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
            override val isUnrelatedSdkDevice: Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE
            override val title: Int = io.github.pknujsp.everyweather.core.resource.R.string.foreground_service_location_permission
            override val message: Int = io.github.pknujsp.everyweather.core.resource.R.string.foreground_service_location_permission_denied
            override val action: Int = io.github.pknujsp.everyweather.core.resource.R.string.grant_permissions
            override val hasRepairAction: Boolean = true
            override val hasRetryAction: Boolean = true
            override val reason: Int =
                io.github.pknujsp.everyweather.core.resource.R.string.foreground_service_location_permission_description

            override fun getPendingIntent(context: Context): PendingIntent {
                return PendingIntent.getActivity(context,
                    this::class.simpleName.hashCode(),
                    getIntent(context),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }

            override fun getIntent(context: Context) = appSettingsIntent(context)

            override fun isAvailable(context: Context): Boolean {
                return context.checkSelfPermission(this)
            }
        }


        data object BackgroundLocation : Permission {
            override val intentAction: String = Settings.ACTION_APPLICATION_DETAILS_SETTINGS

            @RequiresApi(Build.VERSION_CODES.Q) override val permissions: Array<String> =
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            override val isUnrelatedSdkDevice: Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
            override val title: Int = io.github.pknujsp.everyweather.core.resource.R.string.background_location_permission
            override val message: Int = io.github.pknujsp.everyweather.core.resource.R.string.background_location_permission_denied
            override val action: Int = io.github.pknujsp.everyweather.core.resource.R.string.grant_permissions
            override val hasRetryAction: Boolean = true
            override val hasRepairAction: Boolean = true
            override val reason: Int = io.github.pknujsp.everyweather.core.resource.R.string.background_location_permission_description

            override fun getPendingIntent(context: Context): PendingIntent {
                return PendingIntent.getActivity(context,
                    this::class.simpleName.hashCode(),
                    getIntent(context),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }

            override fun getIntent(context: Context) = appSettingsIntent(context)

            override fun isAvailable(context: Context): Boolean {
                return context.checkSelfPermission(this)
            }
        }

        data object PostNotification : Permission {
            override val intentAction: String = Settings.ACTION_APPLICATION_DETAILS_SETTINGS

            @RequiresApi(Build.VERSION_CODES.TIRAMISU) override val permissions: Array<String> =
                arrayOf(Manifest.permission.POST_NOTIFICATIONS)
            override val isUnrelatedSdkDevice: Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
            override val title: Int = io.github.pknujsp.everyweather.core.resource.R.string.post_notification_permission
            override val message: Int = io.github.pknujsp.everyweather.core.resource.R.string.post_notification_permission_denied
            override val action: Int = io.github.pknujsp.everyweather.core.resource.R.string.grant_permissions
            override val hasRetryAction: Boolean = true
            override val hasRepairAction: Boolean = true
            override val reason: Int = io.github.pknujsp.everyweather.core.resource.R.string.post_notification_permission_description

            override fun getPendingIntent(context: Context): PendingIntent {
                return PendingIntent.getActivity(context,
                    this::class.simpleName.hashCode(),
                    getIntent(context),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }

            override fun getIntent(context: Context) = appSettingsIntent(context)

            override fun isAvailable(context: Context): Boolean {
                return context.checkSelfPermission(this)
            }
        }

        data object ScheduleExactAlarm : Permission {
            override val intentAction: String = Settings.ACTION_APPLICATION_DETAILS_SETTINGS

            @RequiresApi(Build.VERSION_CODES.S) override val permissions: Array<String> =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    arrayOf(
                        Manifest.permission.SCHEDULE_EXACT_ALARM,
                        Manifest.permission.USE_EXACT_ALARM,
                    )
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    arrayOf(
                        Manifest.permission.SCHEDULE_EXACT_ALARM,
                    )
                } else {
                    emptyArray()
                }
            override val isUnrelatedSdkDevice: Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.S
            override val title: Int = io.github.pknujsp.everyweather.core.resource.R.string.exact_alarm_permission
            override val message: Int = io.github.pknujsp.everyweather.core.resource.R.string.exact_alarm_permission_denied
            override val action: Int = io.github.pknujsp.everyweather.core.resource.R.string.grant_permissions
            override val hasRetryAction: Boolean = true
            override val hasRepairAction: Boolean = true
            override val reason: Int = io.github.pknujsp.everyweather.core.resource.R.string.exact_alarm_permission_description

            override fun getPendingIntent(context: Context): PendingIntent {
                return PendingIntent.getActivity(context,
                    this::class.simpleName.hashCode(),
                    getIntent(context),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }

            override fun getIntent(context: Context) = appSettingsIntent(context)

            override fun isAvailable(context: Context): Boolean {
                return context.checkSelfPermission(this)
            }
        }
    }

    @SuppressLint("BatteryLife")
    data object BatteryOptimization : FeatureType {
        override val intentAction: String = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        override val title: Int = io.github.pknujsp.everyweather.core.resource.R.string.battery_optimization
        override val message: Int = io.github.pknujsp.everyweather.core.resource.R.string.battery_optimization_enabled
        override val action: Int = io.github.pknujsp.everyweather.core.resource.R.string.open_settings_to_ignore_battery_optimization
        override val hasRetryAction: Boolean = false
        override val hasRepairAction: Boolean = true

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

    }

    data object Network : FeatureType {
        override val intentAction: String = Settings.ACTION_WIRELESS_SETTINGS
        override val title: Int = R.string.network
        override val message: Int = R.string.network_unavailable
        override val action: Int = io.github.pknujsp.everyweather.core.resource.R.string.open_settings_for_network
        override val hasRetryAction: Boolean = true
        override val hasRepairAction: Boolean = true

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
    }

    data object LocationService : FeatureType {
        override val intentAction: String = Settings.ACTION_LOCATION_SOURCE_SETTINGS
        override val title: Int = io.github.pknujsp.everyweather.core.resource.R.string.location_service
        override val message: Int = io.github.pknujsp.everyweather.core.resource.R.string.location_service_disabled
        override val action: Int = io.github.pknujsp.everyweather.core.resource.R.string.open_settings_for_location_service
        override val hasRetryAction: Boolean = true
        override val hasRepairAction: Boolean = true

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
    }

    private companion object {
        fun FeatureType.appSettingsIntent(context: Context) = Intent(intentAction).apply {
            val uri = Uri.fromParts("package", context.packageName, null)
            data = uri
        }

        fun FeatureType.actionIntent() = Intent(intentAction)
    }
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
    val reason: Int? get() = null
    val hasRepairAction: Boolean
    val hasRetryAction: Boolean
}

/**
 * 해당 권한을 부여해야 하는 이유를 설명해야 하는지 확인
 */
fun Activity.shouldShowRequestPermissionRationale(permissionType: FeatureType.Permission): Boolean =
    permissionType.permissions.any { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }


/**
 * 해당 권한을 고려할 필요가 없는 SDK 버전이라면 즉시 true를 반환
 */
fun Context.checkSelfPermission(permissionType: FeatureType.Permission): Boolean =
    permissionType.isUnrelatedSdkDevice || permissionType.permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }