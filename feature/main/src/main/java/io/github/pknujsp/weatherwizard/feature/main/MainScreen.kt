package io.github.pknujsp.weatherwizard.feature.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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
import io.github.pknujsp.weatherwizard.core.ui.theme.AppColorScheme
import io.github.pknujsp.weatherwizard.core.ui.theme.AppShapes
import io.github.pknujsp.weatherwizard.feature.componentservice.notification.HostNotificationScreen
import io.github.pknujsp.weatherwizard.feature.favorite.HostFavoriteScreen
import io.github.pknujsp.weatherwizard.feature.settings.HostSettingsScreen
import io.github.pknujsp.weatherwizard.feature.weather.HostWeatherScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreen(rootNavControllerViewModel: RootNavControllerViewModel = hiltViewModel()) {
    val mainUiState = rememberMainState(rootNavControllerViewModel.requestedRoute)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet(drawerContainerColor = AppColorScheme.surface) {
            mainUiState.tabs.values.forEach {
                DrawerRouteItem(it, mainUiState) {
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

}

@Composable
private fun DrawerRouteItem(
    route: MainRoutes, mainUiState: MainUiState, onClick: () -> Unit
) {
    NavigationDrawerItem(label = {
        Text(
            text = stringResource(id = route.navTitle),
        )
    }, icon = {
        Icon(painter = painterResource(id = route.navIcon), contentDescription = stringResource(id = route.navTitle))
    }, selected = mainUiState.navController.currentDestination?.route == route.route, onClick = onClick)
    Divider()
}