package io.github.pknujsp.everyweather.feature.favorite.state

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.feature.favorite.LocationState
import io.github.pknujsp.everyweather.feature.favorite.model.LocationUiState
import io.github.pknujsp.everyweather.feature.favorite.rememberLocationState


private class MutableLocationUiState(
    private val locationState: LocationState, private val locationUiState: LocationUiState
) : TargetLocationUiState {
    override fun refreshLocation() {
        val isGpsProviderEnabled = locationState.isGpsProviderEnabled
    }

}

@Stable
interface TargetLocationUiState {
    fun refreshLocation()
}

@Composable
fun rememberTargetLocationUiState(
    context: Context = LocalContext.current, locationState: LocationState = rememberLocationState(context), locationUiState: LocationUiState
): TargetLocationUiState {
    val state = remember {
        MutableLocationUiState(locationState, locationUiState)
    }

    return state
}