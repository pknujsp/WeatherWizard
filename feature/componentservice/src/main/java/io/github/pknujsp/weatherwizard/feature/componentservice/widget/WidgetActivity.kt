package io.github.pknujsp.weatherwizard.feature.componentservice.widget

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.window.DialogProperties
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes

@AndroidEntryPoint
class WidgetActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RectangleShape,
                color = BottomSheetDefaults.ScrimColor
            ) {
                AlertDialog(onDismissRequest = {
                    finish()
                }, properties = DialogProperties(decorFitsSystemWindows = false)) {
                    Box(modifier = Modifier.background(Color.White, AppShapes.extraLarge),
                        contentAlignment = androidx.compose.ui.Alignment.Center) {
                        WidgetDialogScreen()
                    }
                }
            }
        }
    }
}