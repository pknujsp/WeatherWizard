package io.github.pknujsp.weatherwizard.feature.widget.activity.configure

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import io.github.pknujsp.weatherwizard.core.model.ActionState
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationType
import io.github.pknujsp.weatherwizard.core.model.coordinate.LocationTypeModel
import io.github.pknujsp.weatherwizard.core.model.widget.WidgetType
import io.github.pknujsp.weatherwizard.core.ui.LocationScreen
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.core.ui.WeatherProvidersScreen
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewsScreen
import io.github.pknujsp.weatherwizard.feature.searchlocation.SearchLocationScreen
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.widgetnotification.widget.WidgetManager
import io.github.pknujsp.weatherwizard.feature.widget.activity.SummaryWeatherWidgetProvider


@Composable
fun WidgetConfigureScreen(navController: NavController, widgetId: Int, widgetType: WidgetType) {
    val context = LocalContext.current
    val viewModel: WidgetConfigureViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.load(widgetId, widgetType)
    }

    val widget = viewModel.widget
    var showSearch by remember { mutableStateOf(false) }
    val widgetManager = remember { WidgetManager.getInstance(context) }
    val actionState = remember { viewModel.action }

    val activity = remember { context as Activity }

    LaunchedEffect(actionState) {
        if (actionState != null) {
            when (actionState) {
                ConfigureActionState.SAVE_SUCCESS -> {
                    createWidgetAndFinish(activity, widgetId, widgetManager)
                }

                ConfigureActionState.NO_LOCATION_IS_SELECTED -> {
                    Toast.makeText(context, context.getString(actionState.message), Toast.LENGTH_SHORT).show()
                }

                else -> {
                }
            }
        }
    }

    if (showSearch) {
        SearchLocationScreen(onSelectedLocation = {
            it?.let { newLocation ->
                widget.location = LocationTypeModel(locationType = LocationType.CustomLocation,
                    address = newLocation.addressName,
                    latitude = newLocation.latitude,
                    country = newLocation.countryName,
                    longitude = newLocation.longitude)
            }
            showSearch = false
        }, popBackStack = {
            showSearch = false
        })
    } else {
        Column {
            TitleTextWithNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.configure_widget)) {
                navController.popBackStack()
            }
            RemoteViewsScreen(widgetManager.remoteViewCreator(widgetType), viewModel.units)

            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LocationScreen(widget.location, onSelectedItem = {
                    widget.location = widget.location.copy(locationType = it)
                }) {
                    showSearch = true
                }
                WeatherProvidersScreen(widget.weatherProvider) {
                    widget.weatherProvider = it
                }
            }

            Box(modifier = Modifier.padding(12.dp)) {
                SecondaryButton(text = stringResource(id = io.github.pknujsp.weatherwizard.core.resource.R.string.save),
                    modifier = Modifier.fillMaxWidth()) {
                    widget.save()
                }
            }
        }
    }

}


fun createWidgetAndFinish(activity: Activity, widgetId: Int, widgetManager: WidgetManager) {
    widgetManager.getInitPendingIntent(activity, intArrayOf(widgetId), SummaryWeatherWidgetProvider::class).send()

    val resultValue = Intent()
    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)

    activity.setResult(Activity.RESULT_OK, resultValue)
    activity.finish()
}

enum class ConfigureActionState : ActionState {
    NO_LOCATION_IS_SELECTED {
        override val message: Int
            get() = io.github.pknujsp.weatherwizard.core.resource.R.string.no_location_is_selected
    },
    SAVE_SUCCESS {
        override val message: Int
            get() = R.string.added_widget
    },
}