package io.github.pknujsp.weatherwizard.feature.widget.activity.configure

import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.pknujsp.weatherwizard.feature.widget.WidgetManager
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType

@AndroidEntryPoint
class SummaryWeatherWidgetConfigureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        intent.extras?.run {
            val widgetId = getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

            if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                finish()
                return
            }

            setContent {
                val navController = rememberNavController()
                val startDestination = WidgetRoutes.Configure.routeWithArguments(widgetId, getInt(WidgetManager.WIDGET_TYPE))
                NavHost(navController = navController, route = WidgetRoutes.route, startDestination = startDestination) {
                    composable(WidgetRoutes.Configure.route, arguments = WidgetRoutes.Configure.arguments) {
                        WidgetConfigureScreen(navController)
                    }
                }
            }
        } ?: run {
            finish()
        }
    }

}