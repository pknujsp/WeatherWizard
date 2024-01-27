package io.github.pknujsp.everyweather.feature.favorite

import android.content.Context
import android.location.LocationManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.core.common.asActivity


private class MutableLocationState(
    private val locationManager: LocationManager
) : LocationState {
    override val isGpsProviderEnabled: Boolean get() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}


@Stable
interface LocationState {
    val isGpsProviderEnabled: Boolean
}

@Composable
fun rememberLocationState(context: Context = LocalContext.current): LocationState {
    val activity = LocalContext.current.asActivity()
    requireNotNull(activity) {
        "activit must not be null"
    }

    val state = remember {
        MutableLocationState(activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
    }

    return state
}