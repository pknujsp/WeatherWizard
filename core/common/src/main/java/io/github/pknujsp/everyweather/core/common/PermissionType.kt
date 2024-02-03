package io.github.pknujsp.everyweather.core.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

enum class PermissionType(val permissions: Array<String>, val isUnrelatedSdkDevice: Boolean, val reason: Int) : StatefulFeature {
    LOCATION(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
        false,
        io.github.pknujsp.everyweather.core.resource.R.string.location_permission_description) {
        override val title: Int = io.github.pknujsp.everyweather.core.resource.R.string.location_permission
        override val message: Int = io.github.pknujsp.everyweather.core.resource.R.string.location_permission_denied
        override val action: Int = io.github.pknujsp.everyweather.core.resource.R.string.grant_permissions
        override val hasRepairAction: Boolean = true
        override val hasRetryAction: Boolean = true
    },

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    FOREGROUND_SERVICE_LOCATION(
        arrayOf(Manifest.permission.FOREGROUND_SERVICE_LOCATION), Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
        io.github.pknujsp.everyweather.core.resource.R.string.foreground_service_location_permission_description,
    ) {
        override val title: Int = io.github.pknujsp.everyweather.core.resource.R.string.foreground_service_location_permission
        override val message: Int = io.github.pknujsp.everyweather.core.resource.R.string.foreground_service_location_permission_denied
        override val action: Int = io.github.pknujsp.everyweather.core.resource.R.string.grant_permissions
        override val hasRepairAction: Boolean = true
        override val hasRetryAction: Boolean = true
    },

    @RequiresApi(Build.VERSION_CODES.Q)
    BACKGROUND_LOCATION(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q,
        io.github.pknujsp.everyweather.core.resource.R.string.background_location_permission_description) {
        override val title: Int = io.github.pknujsp.everyweather.core.resource.R.string.background_location_permission
        override val message: Int = io.github.pknujsp.everyweather.core.resource.R.string.background_location_permission_denied
        override val action: Int = io.github.pknujsp.everyweather.core.resource.R.string.grant_permissions
        override val hasRetryAction: Boolean = true
        override val hasRepairAction: Boolean = true
    },

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    POST_NOTIFICATIONS(arrayOf(Manifest.permission.POST_NOTIFICATIONS),
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU,
        io.github.pknujsp.everyweather.core.resource.R.string.post_notification_permission_description) {
        override val title: Int = io.github.pknujsp.everyweather.core.resource.R.string.post_notification_permission
        override val message: Int = io.github.pknujsp.everyweather.core.resource.R.string.post_notification_permission_denied
        override val action: Int = io.github.pknujsp.everyweather.core.resource.R.string.grant_permissions
        override val hasRetryAction: Boolean = true
        override val hasRepairAction: Boolean = true
    },

    @RequiresApi(Build.VERSION_CODES.S)
    SCHEDULE_EXACT_ALARM_ABOVE_EQUALS_ON_SDK_31(arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM),
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S,
        io.github.pknujsp.everyweather.core.resource.R.string.exact_alarm_permission_description) {
        override val title: Int = io.github.pknujsp.everyweather.core.resource.R.string.exact_alarm_permission
        override val message: Int = io.github.pknujsp.everyweather.core.resource.R.string.exact_alarm_permission_denied
        override val action: Int = io.github.pknujsp.everyweather.core.resource.R.string.grant_permissions
        override val hasRetryAction: Boolean = true
        override val hasRepairAction: Boolean = true
    };
}


/**
 * 해당 권한을 부여해야 하는 이유를 설명해야 하는지 확인
 */
fun Activity.shouldShowRequestPermissionRationale(permissionType: PermissionType): Boolean =
    permissionType.permissions.any { ActivityCompat.shouldShowRequestPermissionRationale(this, it) }


/**
 * 해당 권한을 고려할 필요가 없는 SDK 버전이라면 즉시 true를 반환
 */
fun Context.checkSelfPermission(permissionType: PermissionType): Boolean =
    permissionType.isUnrelatedSdkDevice || permissionType.permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }