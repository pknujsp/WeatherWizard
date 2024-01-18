package io.github.pknujsp.weatherwizard.feature.componentservice.widget.configure

import android.appwidget.AppWidgetManager
import android.content.Intent
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

        val widgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(RESULT_CANCELED, result)

        val widgetType = WidgetType.fromProvider(AppWidgetManager.getInstance(this).getAppWidgetInfo(widgetId).provider)
        val argumentsOfStartDestination = WidgetRoutes.Configure.argumentsWithDefaultValue(widgetId, widgetType.key)

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, route = WidgetRoutes.route, startDestination = WidgetRoutes.Configure.route) {
                composable(WidgetRoutes.Configure.route, arguments = argumentsOfStartDestination) {
                    WidgetConfigureScreen()
                }
            }
        }
    }
}