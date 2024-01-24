package io.github.pknujsp.weatherwizard.core.common.manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest

internal class AppNetworkManagerImpl(context: Context) : AppNetworkManager {
    private val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    override fun registerNetworkCallback(networkCallback: ConnectivityManager.NetworkCallback) {
        unregisterNetworkCallback()
        this.networkCallback = networkCallback
        connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
    }

    override fun unregisterNetworkCallback() {
        networkCallback?.run { connectivityManager.unregisterNetworkCallback(this) }
        networkCallback = null
    }

    override fun isNetworkAvailable(): Boolean = connectivityManager.activeNetwork != null

}

interface AppNetworkManager {
    fun registerNetworkCallback(networkCallback: ConnectivityManager.NetworkCallback)
    fun unregisterNetworkCallback()
    fun isNetworkAvailable(): Boolean
}