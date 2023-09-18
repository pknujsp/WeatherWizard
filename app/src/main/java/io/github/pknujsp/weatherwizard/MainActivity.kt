package io.github.pknujsp.weatherwizard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.weatherwizard.core.common.SystemBarColorMonitor
import io.github.pknujsp.weatherwizard.core.common.SystemBarStyler
import io.github.pknujsp.weatherwizard.core.ui.theme.MainTheme
import io.github.pknujsp.weatherwizard.core.ui.theme.AppColorScheme
import io.github.pknujsp.weatherwizard.feature.main.MainScreen


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: ActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val systemBarStyler = SystemBarStyler(window)
        //val systemBarColorMonitor = SystemBarColorMonitor(window, systemBarStyler, lifecycle)

        setContent {
            MainTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = AppColorScheme.background) {
                    MainScreen()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}