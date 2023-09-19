package io.github.pknujsp.weatherwizard.feature.weather


import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.core.common.LocationPermissionManager
import io.github.pknujsp.weatherwizard.core.common.OpenSettingsForLocationPermission
import io.github.pknujsp.weatherwizard.core.common.UnavailableFeature
import io.github.pknujsp.weatherwizard.core.model.weather.RequestWeatherDataArgs
import io.github.pknujsp.weatherwizard.core.model.weather.common.WeatherDataProvider
import io.github.pknujsp.weatherwizard.core.ui.RoundedButton
import io.github.pknujsp.weatherwizard.core.ui.theme.AppColorScheme
import io.github.pknujsp.weatherwizard.feature.weather.info.WeatherInfoScreen

@Composable
@Preview
fun WeatherMainScreen() {
    val mainViewModel: WeatherMainViewModel = hiltViewModel()
    val navController = rememberNavController()

    var permissionGranted by remember { mutableStateOf(false) }
    var openPermissionActivity by remember { mutableStateOf(false) }
    var unavailable by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableStateOf(0) }

    if (unavailable) {
        UnavailableFeatureScreen(title = R.string.title_why_you_need_permissions,
            unavailableFeature = UnavailableFeature.LOCATION_PERMISSION_DENIED) {
            openPermissionActivity = true
        }
    }

    if (openPermissionActivity) {
        OpenSettingsForLocationPermission {
            Log.d("WeatherMainScreen", "onActivityResult, refreshKey: $refreshKey")
            openPermissionActivity = false
            refreshKey++
        }
    }

    if (permissionGranted) {
        WeatherInfoScreen(RequestWeatherDataArgs(latitude = 35.236323256911774,
            longitude = 128.86341167027018,
            weatherDataProvider = WeatherDataProvider.Kma))
    } else {
        LocationPermissionManager(onPermissionGranted = {
            permissionGranted = true
            openPermissionActivity = false
            unavailable = false
        }, onPermissionDenied = {
            unavailable = true
        }, onShouldShowRationale = {
            unavailable = true
        }, onNeverAskAgain = {
            unavailable = true
        }, refreshKey)
    }
}

@Composable
private fun PermissionDialog(onDismissRequest: () -> Unit, onGrantPermission: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)) {
        Column(
            modifier = Modifier
                .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.Start,
        ) {
            Text(text = stringResource(id = R.string.title_why_you_need_permissions), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = stringResource(id = R.string.description_why_you_need_permissions), style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                RoundedButton(colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = AppColorScheme.primary
                ), text = stringResource(id = R.string.close), onClick = onDismissRequest)
                RoundedButton(text = stringResource(id = R.string.grant_permissions), onClick = onGrantPermission)
            }
        }
    }
}

@Composable
fun UnavailableFeatureScreen(@StringRes title: Int, unavailableFeature: UnavailableFeature, onClick: () -> Unit) {
    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 24.dp)
        .statusBarsPadding()) {
        val (titleCons, messageCons, actionCons) = createRefs()
        Text(text = stringResource(title), style = MaterialTheme.typography.titleLarge, modifier = Modifier.constrainAs(titleCons) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
        })
        Text(text = stringResource(unavailableFeature.message),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.constrainAs(messageCons) {
                top.linkTo(titleCons.bottom, margin = 36.dp)
                start.linkTo(parent.start)
            })
        RoundedButton(text = stringResource(id = unavailableFeature.action),
            onClick = onClick,
            modifier = Modifier.constrainAs(actionCons) {
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            })
    }
}