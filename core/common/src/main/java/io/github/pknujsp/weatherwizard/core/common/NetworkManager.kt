package io.github.pknujsp.weatherwizard.core.common

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

class NetworkManager(context: Context) {
    private companion object {
        var connectivityManager: ConnectivityManager? = null
        var networkCallback: ConnectivityManager.NetworkCallback? = null
    }

    init {
        if (connectivityManager == null) {
            connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
    }


    fun registerNetworkCallback(networkCallback: ConnectivityManager.NetworkCallback) {
        Companion.networkCallback?.run {
            connectivityManager?.unregisterNetworkCallback(this)
        }
        Companion.networkCallback = networkCallback
        connectivityManager?.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
    }

    fun unregisterNetworkCallback() {
        networkCallback?.run { connectivityManager?.unregisterNetworkCallback(this) }
    }

    fun isNetworkAvailable(): Boolean = connectivityManager?.getNetworkCapabilities(connectivityManager?.activeNetwork)?.run {
        hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } ?: false

    @Composable
    fun OpenSettingsForNetwork(onReturnedFromSettings: () -> Unit) {
        val context = LocalContext.current
        val onReturnedFromSettingsState by rememberUpdatedState(onReturnedFromSettings)

        val settingsLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            onReturnedFromSettingsState()
        }

        LaunchedEffect(Unit) {
            settingsLauncher.launch(Intent(Settings.ACTION_WIRELESS_SETTINGS))
        }
    }
}