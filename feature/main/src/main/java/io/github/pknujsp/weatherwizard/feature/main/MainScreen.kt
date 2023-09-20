package io.github.pknujsp.weatherwizard.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.pknujsp.weatherwizard.feature.favorite.HostFavoriteScreen
import io.github.pknujsp.weatherwizard.feature.settings.HostSettingsScreen
import io.github.pknujsp.weatherwizard.feature.weather.HostWeatherScreen


@Preview
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(bottomBar = {
        BottomNavigationBar(backStackEntry, navController)
    }) { paddingValues ->
        NavHost(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
            navController = navController,
            route = MainRoutes.route,
            startDestination = MainRoutes.Weather.route) {
            composable(MainRoutes.Weather.route) {
                HostWeatherScreen()
            }
            composable(MainRoutes.Favorite.route) {
                HostFavoriteScreen()
            }
            composable(MainRoutes.Settings.route) {
                HostSettingsScreen()
            }
        }
    }
}


@Composable
private fun BottomNavigationBar(
    backStackEntry: NavBackStackEntry?, navController: NavHostController
) {
    Row(modifier = Modifier
        .background(Color.White)
        .navigationBarsPadding()
        .padding(vertical = 10.dp)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround) {
        BottomNavigationBarItem(MainRoutes.Weather, backStackEntry, navController)
        BottomNavigationBarItem(MainRoutes.Favorite, backStackEntry, navController)
        BottomNavigationBarItem(MainRoutes.Settings, backStackEntry, navController)
    }
}

@Composable
private fun RowScope.BottomNavigationBarItem(
    route: MainRoutes, backStackEntry: NavBackStackEntry?, navController: NavHostController
) {
    Box(modifier = Modifier
        .weight(1f)
        .clip(CircleShape)
        .background(Color.Transparent)
        .clickable {
            navController.navigate(route.route) {
                launchSingleTop = true
                backStackEntry?.destination?.route?.let {
                    popUpTo(it) {
                        inclusive = true
                    }
                }
            }
        }, contentAlignment = Alignment.Center) {
        Icon(painter = painterResource(id = route.navIcon),
            contentDescription = stringResource(id = route.navTitle),
            modifier = Modifier
                .size(32.dp)
                .padding(4.dp),
            tint = if (backStackEntry?.destination?.route == route.route) Color.Blue else Color.Gray)
    }
}