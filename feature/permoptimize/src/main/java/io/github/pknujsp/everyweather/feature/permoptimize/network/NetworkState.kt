package io.github.pknujsp.everyweather.feature.permoptimize.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.core.common.manager.AppNetworkManager
import io.github.pknujsp.everyweather.core.common.manager.AppNetworkManagerImpl
import io.github.pknujsp.everyweather.feature.permoptimize.feature.AppFeatureState

@Stable
interface NetworkState : AppFeatureState {
    val appNetworkManager: AppNetworkManager
}

@Stable
private class MutableNetworkState(
    override val appNetworkManager: AppNetworkManager,
    override val featureType: FeatureType = FeatureType.Network,
) : NetworkState {
    override var isChanged: Int by mutableIntStateOf(0)
    override var isShowSettingsActivity by mutableStateOf(false)

    override fun hideSettingsActivity() {
        isShowSettingsActivity = false
        isChanged++
    }

    override fun showSettingsActivity() {
        isShowSettingsActivity = true
    }

    override fun isAvailable(context: Context): Boolean {
        return appNetworkManager.isNetworkAvailable()
    }
}

@Composable
fun rememberAppNetworkState(context: Context = LocalContext.current): NetworkState {
    val appNetworkManager =
        remember {
            AppNetworkManagerImpl(context)
        }
    val networkUiState =
        remember {
            MutableNetworkState(appNetworkManager)
        }

    DisposableEffect(appNetworkManager) {
        appNetworkManager.registerNetworkCallback(
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    networkUiState.isChanged++
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    networkUiState.isChanged++
                }
            },
        )

        onDispose {
            appNetworkManager.unregisterNetworkCallback()
        }
    }

    return networkUiState
}
