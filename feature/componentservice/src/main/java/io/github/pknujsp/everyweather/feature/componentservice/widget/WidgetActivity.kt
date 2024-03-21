package io.github.pknujsp.everyweather.feature.componentservice.widget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.everyweather.core.ui.theme.AppColorScheme
import io.github.pknujsp.everyweather.core.ui.theme.AppShapes
import io.github.pknujsp.everyweather.core.ui.theme.MainTheme
import io.github.pknujsp.everyweather.core.ui.theme.setWindowStyle

@AndroidEntryPoint
class WidgetActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setWindowStyle()
        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                BasicAlertDialog(onDismissRequest = {
                    finish()
                }, properties = DialogProperties(decorFitsSystemWindows = false)) {
                    Box(
                        modifier = Modifier.background(Color.White, AppShapes.extraLarge),
                        contentAlignment = androidx.compose.ui.Alignment.Center,
                    ) {
                        WidgetDialogScreen()
                    }
                }
            }
        }
    }
}