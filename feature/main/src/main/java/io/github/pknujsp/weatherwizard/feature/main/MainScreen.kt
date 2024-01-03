package io.github.pknujsp.weatherwizard.feature.main

import android.view.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
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

    NavHost(navController = mainUiState.navController,
        startDestination = MainRoutes.Weather.route,
        route = MainRoutes.route,
        modifier = Modifier.fillMaxSize()) {
        composable(MainRoutes.Weather.route) {
            WeatherMainScreen(mainUiState)
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

@Composable
private fun WeatherMainScreen(mainUiState: MainUiState) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(drawerState = drawerState, gesturesEnabled = true, drawerContent = {
        ModalDrawerSheet(drawerContainerColor = AppColorScheme.surface.copy(alpha = 0.97f), modifier = Modifier.systemBarsPadding()) {
            mainUiState.tabs.values.forEach {
                DrawerRouteItem(it) {
                    scope.launch {
                        mainUiState.navigate(it)
                        drawerState.close()
                    }
                }
            }
            Divider()
            AsyncImage(modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.CenterHorizontally)
                .height(22.dp),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(io.github.pknujsp.weatherwizard.core.resource.R.drawable.textlogo_small).build(),
                contentDescription = null)
        }
    }) {
        HostWeatherScreen {
            scope.launch {
                drawerState.open()
            }
        }
    }
}

@Composable
private fun DrawerRouteItem(
    route: MainRoutes, onClick: () -> Unit
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
        selected = false,
        onClick = onClick,
        shape = RectangleShape,
        colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent,
            selectedTextColor = Color.Gray,
            unselectedBadgeColor = Color.Gray,
            selectedIconColor = Color.Gray,
            unselectedIconColor = Color.Gray))
}