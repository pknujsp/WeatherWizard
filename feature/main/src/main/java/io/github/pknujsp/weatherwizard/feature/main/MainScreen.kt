package io.github.pknujsp.weatherwizard.feature.main

import android.content.res.Resources
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.feature.favorite.FavoriteScreen
import io.github.pknujsp.weatherwizard.feature.main.navigation.MainRoutes
import io.github.pknujsp.weatherwizard.feature.settings.SettingsScreen
import io.github.pknujsp.weatherwizard.feature.weather.WeatherMainScreen


@Preview
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val backStackEntry = navController.currentBackStackEntryAsState()
    val navigationBarHeight = with(LocalDensity.current) {
        (Resources.getSystem().getDimension(Resources.getSystem().getIdentifier("navigation_bar_height", "dimen", "android")) / this
            .density).dp
    }

    Scaffold(modifier = Modifier,
        bottomBar = {
            NavigationBar {
                NavItem(Modifier.windowInsetsBottomHeight(WindowInsets(bottom = 56.dp + navigationBarHeight)),
                    route = MainRoutes.Home,
                    backStackEntry = { backStackEntry.value },
                    navController)
                NavItem(Modifier.windowInsetsBottomHeight(WindowInsets(bottom = 56.dp + navigationBarHeight)),
                    route = MainRoutes.Favorite,
                    backStackEntry = { backStackEntry.value },
                    navController)
                NavItem(Modifier.windowInsetsBottomHeight(WindowInsets(bottom = 56.dp + navigationBarHeight)),
                    route = MainRoutes.Settings,
                    backStackEntry = { backStackEntry.value },
                    navController)
            }
        }) { paddingValues ->

        NavHost(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
            navController = navController,
            startDestination = MainRoutes.Home
                .route) {
            composable(MainRoutes.Home.route) { WeatherMainScreen() }
            composable(MainRoutes.Favorite.route) { FavoriteScreen() }
            composable(MainRoutes.Settings.route) { SettingsScreen() }
        }
    }
}

@Composable
private fun RowScope.NavItem(
    modifier: Modifier,
    route: MainRoutes, backStackEntry: () -> androidx.navigation.NavBackStackEntry?, navController: androidx
    .navigation.NavHostController
) {
    NavigationBarItem(
        icon = {
            Icon(
                painter = painterResource(id = route.navIcon),
                contentDescription = stringResource(id = route.navTitle),
                modifier = Modifier.size(24.dp)
            )
        },
        selected = backStackEntry()?.destination?.route == route.route,
        onClick = { navController.navigate(route.route) },
        alwaysShowLabel = false,
        modifier = modifier
    )
}