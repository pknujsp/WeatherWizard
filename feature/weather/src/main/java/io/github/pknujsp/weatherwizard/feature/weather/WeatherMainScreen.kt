package io.github.pknujsp.weatherwizard.feature.weather


import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.core.common.LocationPermissionManager
import io.github.pknujsp.weatherwizard.core.common.OpenSettingsForLocationPermission
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

    val view = LocalView.current
    LaunchedEffect(Unit) {
        (view.context as Activity).window.run {
            WindowCompat.getInsetsController(this, decorView).apply {
                isAppearanceLightStatusBars = false
            }
        }
    }

    var permissionStatus by remember { mutableStateOf(false) }
    var openSettings by remember { mutableStateOf(false) }
    var openPermissionDialog by remember { mutableStateOf(false) }

    if (openPermissionDialog) {
        PermissionDialog(onDismissRequest = {
            openPermissionDialog = false
        }, onGrantPermission = {
            openPermissionDialog = false
            permissionStatus = true
            openSettings = true
        })
    }

    if (openSettings) {
        OpenSettingsForLocationPermission {
            openSettings = false
            permissionStatus = false
        }
    }

    if (permissionStatus) {
        WeatherInfoScreen(RequestWeatherDataArgs(latitude = 35.236323256911774,
            longitude = 128.86341167027018,
            weatherDataProvider = WeatherDataProvider.Kma))
    } else {
        LocationPermission(permissionStatus = {
            permissionStatus = it
        }, openSettings = {
            openSettings = it
        }, openPermissionDialog = {
            openPermissionDialog = true
        })
    }
}

@Composable
private fun LocationPermission(
    permissionStatus: (Boolean) -> Unit,
    openSettings: (Boolean) -> Unit,
    openPermissionDialog: () -> Unit
) {
    LocationPermissionManager(onPermissionGranted = {
        permissionStatus(true)
    }, onPermissionDenied = {
        permissionStatus(false)
    }, onShouldShowRationale = {
        openPermissionDialog()
    }, onNeverAskAgain = {
        openSettings(true)
    })
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