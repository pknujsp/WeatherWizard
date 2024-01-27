package io.github.pknujsp.everyweather.core.ui.feature

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.core.common.manager.AppNetworkManager
import io.github.pknujsp.everyweather.core.common.manager.AppNetworkManagerImpl

@Stable
interface NetworkState {
    val isNetworkAvailable: Boolean
    val appNetworkManager: AppNetworkManager
}

private class MutableNetworkState(override val appNetworkManager: AppNetworkManager) : NetworkState {
    override var isNetworkAvailable by mutableStateOf(appNetworkManager.isNetworkAvailable())
}

@Composable
fun rememberAppNetworkState(context: Context = LocalContext.current): NetworkState {
    val appNetworkManager = remember {
        AppNetworkManagerImpl(context)
    }
    val networkUiState = remember {
        MutableNetworkState(appNetworkManager)
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