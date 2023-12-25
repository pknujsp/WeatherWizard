package io.github.pknujsp.weatherwizard.feature.main

import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.weatherwizard.core.common.FeatureType
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcher
import io.github.pknujsp.weatherwizard.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.weatherwizard.core.common.manager.AppNetworkManager
import io.github.pknujsp.weatherwizard.core.ui.feature.OpenAppSettingsActivity
import io.github.pknujsp.weatherwizard.core.ui.feature.UnavailableFeatureScreen
import io.github.pknujsp.weatherwizard.core.ui.theme.AppColorScheme
import io.github.pknujsp.weatherwizard.core.ui.theme.MainTheme
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.WidgetStarter
import io.github.pknujsp.weatherwizard.feature.map.MapInitializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ActivityViewModel by viewModels()

    @Inject @CoDispatcher(CoDispatcherType.SINGLE) lateinit var dispatcher: CoroutineDispatcher
    @Inject lateinit var appNetworkManager: AppNetworkManager
    @Inject lateinit var widgetStarter: WidgetStarter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowStyle()

        setContent {
            LaunchedEffect(Unit) {
                launch(dispatcher) {
                    MapInitializer.initialize(applicationContext)
                    viewModel.notificationStarter.start(applicationContext)
                    widgetStarter.start(applicationContext)
                    Log.d("MainActivity", "initialized map, notification, widget")
                }
            }

            MainTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = AppColorScheme.background) {
                    var isNetworkAvailable by remember { mutableStateOf(appNetworkManager.isNetworkAvailable()) }
                    var openNetworkSettings by remember { mutableStateOf(false) }

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

    private fun setWindowStyle() {
        window.run {
            WindowCompat.setDecorFitsSystemWindows(this, false)
            WindowCompat.getInsetsController(this, decorView).run {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }

            statusBarColor = Color.TRANSPARENT
            navigationBarColor = Color.TRANSPARENT

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                isStatusBarContrastEnforced = false
                isNavigationBarContrastEnforced = false
            }
        }
    }
}