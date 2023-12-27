package io.github.pknujsp.weatherwizard.feature.main

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.core.ui.MainRoutes
import io.github.pknujsp.weatherwizard.core.ui.RootNavControllerViewModel
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.HostNotificationScreen
import io.github.pknujsp.weatherwizard.feature.favorite.HostFavoriteScreen
import io.github.pknujsp.weatherwizard.feature.settings.HostSettingsScreen
import io.github.pknujsp.weatherwizard.feature.weather.HostWeatherScreen
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


@Composable
fun MainScreen() {
    val rootNavController = rememberNavController()
    val backStackEntry by rootNavController.currentBackStackEntryAsState()
    val rootNavControllerViewModel: RootNavControllerViewModel =
        hiltViewModel(viewModelStoreOwner = (LocalContext.current as ComponentActivity))
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            rootNavControllerViewModel.requestedRoute.collect { newRoute ->
                rootNavController.navigate(newRoute.route) {
                    launchSingleTop = true
                    backStackEntry?.destination?.route?.let {
                        popUpTo(it) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()) {
        TopNavBar(backStackEntry, rootNavController)
        NavHost(navController = rootNavController, route = MainRoutes.route, startDestination = MainRoutes.Weather.route) {
            composable(MainRoutes.Weather.route) {
                HostWeatherScreen()
            }
            composable(MainRoutes.Favorite.route) {
                HostFavoriteScreen()
            }
            composable(MainRoutes.Notification.route) {
                HostNotificationScreen()
            }
            composable(MainRoutes.Settings.route) {
                HostSettingsScreen()
            }
        }
    }
}

@Composable
private fun TopNavBar(
    backStackEntry: NavBackStackEntry?, navController: NavHostController
) {
    Row(modifier = Modifier
        .background(Color.White)
        .padding(start = 12.dp)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start) {
        TopNavBarItem(MainRoutes.Weather, backStackEntry, navController)
        TopNavBarItem(MainRoutes.Favorite, backStackEntry, navController)
        TopNavBarItem(MainRoutes.Notification, backStackEntry, navController)
        TopNavBarItem(MainRoutes.Settings, backStackEntry, navController)
    }
}

@Composable
private fun TopNavBarItem(
    route: MainRoutes, backStackEntry: NavBackStackEntry?, navController: NavHostController
) {
    TextButton(onClick = {
        if (backStackEntry?.destination?.route != route.route) {
            navController.navigate(route.route) {
                launchSingleTop = true
                backStackEntry?.destination?.route?.let {
                    popUpTo(it) {
                        inclusive = true
                    }
                }
            }
        }
    },
        border = null,
        contentPadding = PaddingValues(start = 6.dp, top = 14.dp, end = 4.dp, bottom = 14.dp),
        colors = ButtonDefaults.textButtonColors(contentColor = if (backStackEntry?.destination?.route == route.route) Color.Black else Color(
            0xFF666666), containerColor = Color.Transparent)) {
        Text(text = stringResource(id = route.navTitle),
            fontSize = 19.sp,
            fontWeight = FontWeight(400),
            letterSpacing = (-1).sp,
            lineHeight = 24.sp,
            textDecoration = if (backStackEntry?.destination?.route == route.route) TextDecoration.Underline else TextDecoration.None)
    }
}