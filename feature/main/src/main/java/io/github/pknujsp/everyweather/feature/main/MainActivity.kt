package io.github.pknujsp.everyweather.feature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.everyweather.core.ads.AdMob
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcher
import io.github.pknujsp.everyweather.core.common.coroutines.CoDispatcherType
import io.github.pknujsp.everyweather.core.ui.theme.AppColorScheme
import io.github.pknujsp.everyweather.core.ui.theme.MainTheme
import io.github.pknujsp.everyweather.core.ui.theme.setWindowStyle
import io.github.pknujsp.everyweather.feature.map.OsmdroidInitializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: ActivityViewModel by viewModels()
    @CoDispatcher(CoDispatcherType.IO) @Inject lateinit var ioDispatcher: CoroutineDispatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowStyle()
        setContent {
            val coroutineScope = rememberCoroutineScope()
            LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
                coroutineScope.launch(ioDispatcher) {
                    AdMob.initialize(application)
                    OsmdroidInitializer.initialize(application)
                }
            }

            MainTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = AppColorScheme.background) {
                    MainScreen()
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