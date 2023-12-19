package io.github.pknujsp.weatherwizard.feature.componentservice.widget.configure

import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.feature.componentservice.widget.worker.fromProvider

@AndroidEntryPoint
class WidgetConfigureActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val widgetId = intent.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        val widgetType = AppWidgetManager.getInstance(this).getAppWidgetInfo(widgetId).let {
            WidgetType.fromProvider(it.provider)
        }

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, route = WidgetRoutes.route, startDestination = WidgetRoutes.Configure.route) {
                composable(WidgetRoutes.Configure.route) {
                    WidgetConfigureScreen(navController, widgetId, widgetType)
                }
            }
        }
    }
}