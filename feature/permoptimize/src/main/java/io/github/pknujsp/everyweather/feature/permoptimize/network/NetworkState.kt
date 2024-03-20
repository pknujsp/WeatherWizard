package io.github.pknujsp.everyweather.feature.permoptimize.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.pknujsp.everyweather.core.common.FeatureType
import io.github.pknujsp.everyweather.feature.permoptimize.BaseFeatureStateManager

@SuppressLint("MissingPermission")
@Stable
private class NetworkStateManagerImpl(context: Context, override val featureType: FeatureType = FeatureType.Network) :
    NetworkStateManager() {

    private val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    fun registerNetworkCallback(networkCallback: ConnectivityManager.NetworkCallback) {
        unregisterNetworkCallback()
        this.networkCallback = networkCallback
        connectivityManager.registerNetworkCallback(NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).build(), networkCallback)
    }

    fun unregisterNetworkCallback() {
        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
            networkCallback = null
        }
    }

}

@Stable
abstract class NetworkStateManager : BaseFeatureStateManager()

@Composable
fun rememberNetworkStateManager(context: Context = LocalContext.current): NetworkStateManager {
    val appNetworkManager = remember {
        NetworkStateManagerImpl(context)
    }

    DisposableEffect(appNetworkManager) {
        appNetworkManager.registerNetworkCallback(
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    appNetworkManager.onChanged()
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    appNetworkManager.onChanged()
                }
            },
        )

        onDispose {
            appNetworkManager.unregisterNetworkCallback()
        }
    }

    return appNetworkManager
}