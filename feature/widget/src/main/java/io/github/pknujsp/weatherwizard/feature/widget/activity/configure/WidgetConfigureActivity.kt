package io.github.pknujsp.weatherwizard.feature.widget.activity.configure

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.feature.widget.extension.fromProvider
import io.github.pknujsp.weatherwizard.feature.widget.summary.SummaryWeatherWidgetProvider

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
            val startDestination = WidgetRoutes.Configure.route
            NavHost(navController = navController, route = WidgetRoutes.route, startDestination = startDestination) {
                composable(WidgetRoutes.Configure.route) {
                    WidgetConfigureScreen(navController, widgetId, widgetType)
                }
            }
        }
    }
}