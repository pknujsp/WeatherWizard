package io.github.pknujsp.weatherwizard.feature.main

import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.common.manager.AppNetworkManager
import io.github.pknujsp.weatherwizard.core.ui.feature.OpenAppSettingsActivity
import io.github.pknujsp.weatherwizard.core.ui.feature.UnavailableFeatureScreen
import io.github.pknujsp.weatherwizard.core.ui.theme.AppColorScheme
import io.github.pknujsp.weatherwizard.core.ui.theme.MainTheme
import io.github.pknujsp.weatherwizard.feature.map.MapInitializer
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ActivityViewModel by viewModels()

    @Inject @CoDispatcher(CoDispatcherType.DEFAULT) lateinit var dispatcher: CoroutineDispatcher
    @Inject lateinit var appNetworkManager: AppNetworkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = AppColorScheme.background) {
                    var isNetworkAvailable by remember { mutableStateOf(appNetworkManager.isNetworkAvailable()) }
                    var openNetworkSettings by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        MapInitializer.initialize(applicationContext)
                        viewModel.notificationStarter.start(applicationContext)
                    }

                    DisposableEffect(appNetworkManager) {
                        appNetworkManager.registerNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                            override fun onAvailable(network: Network) {
                                super.onAvailable(network)
                                isNetworkAvailable = true
                            }

                            override fun onLost(network: Network) {
                                super.onLost(network)
                                isNetworkAvailable = appNetworkManager.isNetworkAvailable()
                            }
                        })

                        onDispose {
                            appNetworkManager.unregisterNetworkCallback()
                        }
                    }

                    if (isNetworkAvailable) {
                        MainScreen()
                    } else {
                        UnavailableFeatureScreen(featureType = FeatureType.NETWORK) {
                            openNetworkSettings = true
                        }
                    }

                    if (openNetworkSettings) {
                        OpenAppSettingsActivity(featureType = FeatureType.NETWORK) {
                            openNetworkSettings = false
                        }
                    }
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.startCacheCleaner()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopCacheCleaner()
    }
}