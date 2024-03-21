package io.github.pknujsp.everyweather.feature.favorite

import android.content.Context
import android.location.LocationManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.core.common.asActivity
import io.github.pknujsp.everyweather.core.common.manager.AppComponentManagerFactory
import io.github.pknujsp.everyweather.core.common.manager.AppLocationManager

private class MutableLocationState(
    private val locationManager: AppLocationManager,
) : LocationState {
    override val isGpsProviderEnabled: Boolean get() = locationManager.isGpsProviderEnabled
}

@Stable
interface LocationState {
    val isGpsProviderEnabled: Boolean
}

@Composable
fun rememberLocationState(context: Context = LocalContext.current): LocationState {
    val state = remember {
        MutableLocationState(AppComponentManagerFactory.getManager(context, AppComponentManagerFactory.LOCATION_MANAGER))
    }
    return state
}