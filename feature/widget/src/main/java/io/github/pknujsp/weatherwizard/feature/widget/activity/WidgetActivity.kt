package io.github.pknujsp.weatherwizard.feature.widget.activity

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.weatherwizard.core.ui.theme.AppColorScheme
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes

@AndroidEntryPoint
class WidgetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(0))
        setContent {
            Surface(
                color = Color.Transparent,
                modifier = Modifier.fillMaxSize(),
            ) {
                WidgetDialogScreen()
            }
        }
    }
}