package io.github.pknujsp.weatherwizard.core.common.manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class AppNetworkManager private constructor(context: Context) {
    private val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    companion object {
        private var instance: AppNetworkManager? = null

        fun getInstance(context: Context): AppNetworkManager {
            if (instance == null) {
                instance = AppNetworkManager(context)
            }
            return instance!!
        }
    }


    fun registerNetworkCallback(networkCallback: ConnectivityManager.NetworkCallback) {
        unregisterNetworkCallback()
        this.networkCallback = networkCallback
        connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
    }

    fun unregisterNetworkCallback() {
        networkCallback?.run { connectivityManager.unregisterNetworkCallback(this) }
    }

    fun isNetworkAvailable(): Boolean = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } ?: false

}