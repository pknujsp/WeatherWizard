package io.github.pknujsp.weatherwizard.feature.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.pknujsp.weatherwizard.core.ui.MainRoutes
import io.github.pknujsp.weatherwizard.core.ui.RootNavControllerViewModel
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.HostNotificationScreen
import io.github.pknujsp.weatherwizard.feature.favorite.HostFavoriteScreen
import io.github.pknujsp.weatherwizard.feature.settings.HostSettingsScreen
import io.github.pknujsp.weatherwizard.feature.weather.HostWeatherScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun MainScreen(rootNavControllerViewModel: RootNavControllerViewModel = hiltViewModel()) {
    val mainUiState = rememberMainState(rootNavControllerViewModel.requestedRoute)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        snapshotFlow {
            drawerState.offset
        }.collectLatest {

        }
    }

    DismissibleNavigationDrawer(drawerState = drawerState, gesturesEnabled = true, drawerContent = {
        DismissibleDrawerSheet(drawerContainerColor = Color.LightGray) {
            mainUiState.tabs.values.forEach {
                DrawerRouteItem(it, mainUiState.navController.currentDestination?.route ?: "") {
                    scope.launch {
                        mainUiState.navigate(it)
                        drawerState.close()
                    }
                }
            }
            AsyncImage(modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .height(24.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(io.github.pknujsp.weatherwizard.core.resource.R.drawable.textlogo_small).build(),
                contentDescription = null)
        }
    }) {
        Column(modifier = Modifier.fillMaxSize()) {
            NavHost(navController = mainUiState.navController,
                startDestination = MainRoutes.Weather.route,
                route = MainRoutes.route,
                modifier = Modifier.fillMaxSize()) {
                composable(MainRoutes.Weather.route) {
                    HostWeatherScreen {
                        scope.launch {
                            drawerState.open()
                        }
                    }
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

}

@Composable
private fun DrawerRouteItem(
    route: MainRoutes, currentDestination: String, onClick: () -> Unit
) {
    NavigationDrawerItem(label = {
        Text(
            text = stringResource(id = route.navTitle),
        )
    },
        icon = {
            Icon(modifier = Modifier.size(24.dp),
                painter = painterResource(id = route.navIcon),
                contentDescription = stringResource(id = route.navTitle))
        },
        selected = currentDestination == route.route,
        onClick = onClick,
        shape = RectangleShape,
        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent))
    Divider()
}