package io.github.pknujsp.weatherwizard.core.ui.feature

import android.net.ConnectivityManager
import android.net.Network
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.pknujsp.weatherwizard.core.common.manager.AppNetworkManager

@Stable
interface NetworkUiState {
    val isNetworkAvailable: Boolean
    var isOpenAppSettings: Boolean
}

@Stable
private class MutableNetworkUiState(appNetworkManager: AppNetworkManager) : NetworkUiState {
    override var isNetworkAvailable by mutableStateOf(appNetworkManager.isNetworkAvailable())
    override var isOpenAppSettings by mutableStateOf(false)
}

@Composable
fun rememberAppNetworkState(appNetworkManager: AppNetworkManager): NetworkUiState {
    val networkUiState = remember(appNetworkManager) {
        MutableNetworkUiState(appNetworkManager)
    }

    DisposableEffect(appNetworkManager) {
        appNetworkManager.registerNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                networkUiState.isNetworkAvailable = true
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                networkUiState.isNetworkAvailable = appNetworkManager.isNetworkAvailable()
            }
        })

        onDispose {
            appNetworkManager.unregisterNetworkCallback()
        }
    }

    return networkUiState
}