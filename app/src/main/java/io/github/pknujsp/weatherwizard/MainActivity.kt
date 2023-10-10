package io.github.pknujsp.weatherwizard

import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.weatherwizard.core.common.NetworkManager
import io.github.pknujsp.weatherwizard.core.common.R
import io.github.pknujsp.weatherwizard.core.common.SystemBarStyler
import io.github.pknujsp.weatherwizard.core.common.UnavailableFeature
import io.github.pknujsp.weatherwizard.core.ui.UnavailableFeatureScreen
import io.github.pknujsp.weatherwizard.core.ui.theme.AppColorScheme
import io.github.pknujsp.weatherwizard.core.ui.theme.MainTheme
import io.github.pknujsp.weatherwizard.feature.main.MainScreen


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ActivityViewModel by viewModels()
    private var _networkManager: NetworkManager? = null
    private val networkManager: NetworkManager get() = _networkManager!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val systemBarStyler = SystemBarStyler(window)
        //val systemBarColorMonitor = SystemBarColorMonitor(window, systemBarStyler, lifecycle)
        _networkManager = NetworkManager(applicationContext)

        setContent {
            MainTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = AppColorScheme.background) {
                    var networkAvailable by remember { mutableStateOf(networkManager.isNetworkAvailable()) }
                    var openNetworkSettings by remember { mutableStateOf(false) }

                    DisposableEffect(networkManager) {
                        networkManager.registerNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                            override fun onAvailable(network: Network) {
                                super.onAvailable(network)
                                networkAvailable = true
                            }

                            override fun onLost(network: Network) {
                                super.onLost(network)
                                networkAvailable = networkManager.isNetworkAvailable()
                            }
                        })

                        onDispose {
                            networkManager.unregisterNetworkCallback()
                        }
                    }

                    if (networkAvailable) {
                        MainScreen()
                    } else {
                        UnavailableFeatureScreen(title = R.string.title_network_is_unavailable, unavailableFeature =
                        UnavailableFeature.NETWORK_UNAVAILABLE
                        ) {
                            openNetworkSettings = true
                        }
                    }

                    if (openNetworkSettings) {
                        networkManager.OpenSettingsForNetwork {
                            openNetworkSettings = false
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _networkManager = null
    }
}