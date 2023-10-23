package io.github.pknujsp.weatherwizard.feature.widget.activity.configure

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
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
import io.github.pknujsp.weatherwizard.core.model.favorite.LocationType
import io.github.pknujsp.weatherwizard.core.model.notification.daily.DailyNotificationInfo
import io.github.pknujsp.weatherwizard.core.model.notification.enums.DailyNotificationType
import io.github.pknujsp.weatherwizard.core.model.onSuccess
import io.github.pknujsp.weatherwizard.core.ui.BottomSheetSettingItem
import io.github.pknujsp.weatherwizard.core.ui.SecondaryButton
import io.github.pknujsp.weatherwizard.core.ui.TitleTextWithNavigation
import io.github.pknujsp.weatherwizard.core.ui.dialog.DialogScreen
import io.github.pknujsp.weatherwizard.feature.notification.R
import io.github.pknujsp.weatherwizard.feature.searchlocation.SearchLocationScreen
import io.github.pknujsp.weatherwizard.feature.widget.WidgetType
import io.github.pknujsp.weatherwizard.feature.widget.summary.updateAppWidget


@Composable
fun WidgetConfigureScreen(widgetId: Int, widgetType: WidgetType) {
    val activity = androidx.compose.ui.platform.LocalContext.current as ComponentActivity
    val viewModel: WidgetConfigureViewModel = hiltViewModel()
    val notification by viewModel.notification.collectAsStateWithLifecycle()

    notification.onSuccess { info ->
        val units by viewModel.units.collectAsStateWithLifecycle()
        var showSearch by remember { mutableStateOf(false) }
        val context = LocalContext.current

        if (info.onSaved) {
            LaunchedEffect(Unit) {
                scheduleAlarm(context, info)
                navController.popBackStack()
            }
        }

        if (showSearch) {
            SearchLocationScreen(onSelectedLocation = {
                it?.let { newLocation ->
                    info.apply {
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
                TitleTextWithNavigation(title = stringResource(id = R.string.add_or_edit_daily_notification)) {
                    navController.popBackStack()
                }
                var selectedType by remember { mutableStateOf(info.type) }
                RemoteViewsScreen(RemoteViewsCreatorManager.createRemoteViewsCreator(selectedType), units)

                Column(modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    NotificationTypeItem(selectedType) {
                        selectedType = it
                        info.type = it
                    }
                    TimeItem(info)
                    LocationScreen(info) {
                        showSearch = true
                    }
                    WeatherProvidersScreen(info)
                }

                Box(modifier = Modifier.padding(12.dp)) {
                    SecondaryButton(text = stringResource(id = io.github.pknujsp.weatherwizard.core.common.R.string.save),
                        modifier = Modifier.fillMaxWidth()) {
                        viewModel.save()
                    }
                }
            }
        }
    }
}


fun createWidget(activity: ComponentActivity, widgetId: Int) {
    val appWidgetManager = AppWidgetManager.getInstance(activity)
    updateAppWidget(activity, appWidgetManager, widgetId)

    val resultValue = Intent()
    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)

    activity.setResult(ComponentActivity.RESULT_OK, resultValue)
    activity.finish()
}