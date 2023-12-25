package io.github.pknujsp.weatherwizard.feature.componentservice.widget.configure

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
import io.github.pknujsp.weatherwizard.core.resource.R
import io.github.pknujsp.weatherwizard.core.ui.LocationScreen
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.core.ui.WeatherProvidersScreen
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.ComponentServiceAction
import io.github.pknujsp.weatherwizard.core.widgetnotification.model.LoadWidgetDataArgument
import io.github.pknujsp.weatherwizard.core.widgetnotification.remoteview.RemoteViewsCreatorManager
import io.github.pknujsp.weatherwizard.feature.componentservice.ComponentPendingIntentManager
import io.github.pknujsp.weatherwizard.feature.componentservice.RemoteViewsScreen
import io.github.pknujsp.weatherwizard.feature.searchlocation.SearchLocationScreen


@Composable
fun WidgetConfigureScreen(
    navController: NavController, viewModel: WidgetConfigureViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val widget = viewModel.widget
    var showSearch by remember { mutableStateOf(false) }
    val actionState = viewModel.action

    LaunchedEffect(actionState) {
        if (actionState != null) {
            when (actionState) {
                ConfigureActionState.SAVE_SUCCESS -> {
                    createWidgetAndFinish(context as Activity, widget.widgetId)
                }

                ConfigureActionState.NO_LOCATION_IS_SELECTED -> {
                    Toast.makeText(context, context.getString(actionState.message), Toast.LENGTH_SHORT).show()
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
            TitleTextWithNavigation(title = stringResource(id = R.string.configure_widget)) {
                (context as Activity).finish()
            }
            RemoteViewsScreen(RemoteViewsCreatorManager.getByWidgetType(widget.widgetType), viewModel.units)
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
                SecondaryButton(text = stringResource(id = R.string.save), modifier = Modifier.fillMaxWidth()) {
                    widget.save()
                }
            }
        }
    }

}


fun createWidgetAndFinish(activity: Activity, widgetId: Int) {
    val newWidgetIntent = ComponentPendingIntentManager.getIntent(activity.applicationContext,
        LoadWidgetDataArgument(LoadWidgetDataArgument.NEW_WIDGET, widgetId))
    activity.sendBroadcast(newWidgetIntent)

    val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
    activity.setResult(Activity.RESULT_OK, result)
    activity.finish()
}

enum class ConfigureActionState : ActionState {
    NO_LOCATION_IS_SELECTED {
        override val message: Int
            get() = R.string.no_location_is_selected
    },
    SAVE_SUCCESS {
        override val message: Int
            get() = R.string.added_widget
    },
}