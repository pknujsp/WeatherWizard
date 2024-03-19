package io.github.pknujsp.everyweather.feature.componentservice.widget.configure

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.everyweather.core.model.widget.WidgetType
import io.github.pknujsp.everyweather.core.ui.theme.AppColorScheme
import io.github.pknujsp.everyweather.core.ui.theme.AppShapes
import io.github.pknujsp.everyweather.core.ui.theme.MainTheme
import io.github.pknujsp.everyweather.core.ui.theme.setWindowStyle
import io.github.pknujsp.everyweather.feature.componentservice.widget.WidgetDialogScreen
import io.github.pknujsp.everyweather.feature.componentservice.widget.worker.fromComponentName

@AndroidEntryPoint
class WidgetConfigureActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowStyle()

        val widgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(RESULT_CANCELED, result)

        val widgetType = WidgetType.fromComponentName(AppWidgetManager.getInstance(this).getAppWidgetInfo(widgetId).provider)
        val argumentsOfStartDestination = WidgetRoutes.Configure.argumentsWithDefaultValue(widgetId, widgetType.key)

        setContent {
            MainTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = AppColorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(modifier = Modifier.systemBarsPadding(),
                        navController = navController,
                        route = WidgetRoutes.route,
                        startDestination = WidgetRoutes.Configure.route) {
                        composable(WidgetRoutes.Configure.route, arguments = argumentsOfStartDestination) {
                            WidgetConfigureScreen()
                        }
                    }
                }
            }
        }
    }
}