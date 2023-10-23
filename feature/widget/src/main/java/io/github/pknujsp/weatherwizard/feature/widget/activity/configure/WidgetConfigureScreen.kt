package io.github.pknujsp.weatherwizard.feature.widget.activity.configure

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import io.github.pknujsp.weatherwizard.core.model.ActionState
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.ui.LocationScreen
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.core.ui.WeatherProvidersScreen
import io.github.pknujsp.weatherwizard.core.ui.remoteview.RemoteViewsScreen
import io.github.pknujsp.weatherwizard.feature.notification.R
import io.github.pknujsp.weatherwizard.feature.searchlocation.SearchLocationScreen
import io.github.pknujsp.weatherwizard.feature.widget.WidgetManager
import io.github.pknujsp.weatherwizard.feature.widget.summary.updateAppWidget


@Composable
fun WidgetConfigureScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = remember { context as ComponentActivity }
    val viewModel: WidgetConfigureViewModel = hiltViewModel()

    val units by viewModel.units.collectAsStateWithLifecycle()
    val widget = remember { viewModel.widget }
    var showSearch by remember { mutableStateOf(false) }
    val widgetManager = remember { WidgetManager() }
    val actionState by viewModel.action.collectAsStateWithLifecycle()

    if (widget.onSaved) {
        LaunchedEffect(Unit) {
            createWidgetAndFinish(activity, widget.id)
        }
    }

    actionState?.let {
        Toast.makeText(context, stringResource(id = it.message), Toast.LENGTH_SHORT).show()
    }

    if (showSearch) {
        SearchLocationScreen(onSelectedLocation = {
            it?.let { newLocation ->
                widget.apply {
                    locationType = LocationType.CustomLocation()
                    latitude = newLocation.latitude
                    longitude = newLocation.longitude
                    addressName = newLocation.addressName
                }
            }
            showSearch = false
        }, popBackStack = {
            showSearch = false
        })
    } else {
        Column {
            TitleTextWithNavigation(title = stringResource(id = io.github.pknujsp.weatherwizard.feature.widget.R.string.configure_widget)) {
                navController.popBackStack()
            }
            RemoteViewsScreen(widgetManager.remoteViewCreator(widget.widgetType), units)

            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LocationScreen(widget.locationType, onSelectedItem = {
                    widget.locationType = it
                }) {
                    showSearch = true
                }
                WeatherProvidersScreen(widget.weatherProvider) {
                    widget.weatherProvider = it
                }
            }

            Box(modifier = Modifier.padding(12.dp)) {
                SecondaryButton(text = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.save),
                    modifier = Modifier.fillMaxWidth()) {
                    widget.save()
                }
            }
        }
    }

}


fun createWidgetAndFinish(activity: ComponentActivity, widgetId: Int) {
    val appWidgetManager = AppWidgetManager.getInstance(activity)
    updateAppWidget(activity, appWidgetManager, widgetId)

    val resultValue = Intent()
    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)

    activity.setResult(ComponentActivity.RESULT_OK, resultValue)
    activity.finish()
}

enum class ConfigureActionState : ActionState {
    NO_LOCATION_IS_SELECTED {
        override val message: Int
            get() = io.github.pknujsp.weatherwizard.core.common.R.string.no_location_is_selected
    },
}